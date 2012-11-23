package com.github.tototoshi.http

import java.io.File
import java.util.{ ArrayList, List => JList }
import org.apache.http.client.HttpClient
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.{ HttpGet, HttpPost, HttpUriRequest }
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.entity.mime.content.{ StringBody, FileBody }
import org.apache.http.entity.mime.{ MultipartEntity, HttpMultipartMode }
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.apache.http.{ NameValuePair, HttpEntity, HttpRequest }


trait Request {

  protected var headers: Map[String, String] = Map.empty

  protected var params: Map[String, String] = Map.empty

  protected var _encoding: String = "utf-8"

  def execute(): Response

}

trait RequestBuilder { self: Request =>

  def param(key: String, value: String): this.type = {
    params += (key -> value)
    this
  }

  def header(key: String, value: String): this.type = {
    headers += (key -> value)
    this
  }

  def encoding(e: String): this.type = {
    _encoding = e
    this
  }

  protected def constructNameValuePairs(data: Iterable[(String, String)]): JList[NameValuePair] = {
    data.foldLeft(new ArrayList[NameValuePair](data.size)) {
      case (pairs, (k, v)) => { pairs.add(new BasicNameValuePair(k, v)); pairs }
    }
  }

}

class UrlEncodedGetRequest(client: HttpClient, url: String) extends RequestBuilder with Request {

  def execute(): Response = {
    val urlWithParams = if (params.isEmpty) url else url + "?" +
    URLEncodedUtils.format(constructNameValuePairs(params), _encoding)
    val request = new HttpGet(urlWithParams)
    headers.foreach { case (k, v) => request.addHeader(k, v) }
    new Response(client.execute(request))
  }

}

class UrlEncodedPostRequest(client: HttpClient, url: String) extends RequestBuilder with Request {

  def execute(): Response = {
    val request = new HttpPost(url)
    headers.foreach { case (k, v) => request.addHeader(k, v) }
    request.setEntity(new UrlEncodedFormEntity(constructNameValuePairs(params), _encoding))
    new Response(client.execute(request))
  }

}


sealed trait Part[A]
case class StringPart(key: String, value: String) extends Part[String]
case class FilePart(key: String, value: File) extends Part[File]

trait MultiPartRequest extends Request {

  var parts: Seq[Part[_]] = Seq()

}

trait PartConverter[A] {
  def convert(key: String, value: A): Part[A]
}

trait MultiPartRequestBuilder { self: MultiPartRequest =>

  def part[A](key: String, value: A)(implicit converter: PartConverter[A]): this.type = {
    parts :+= converter.convert(key, value)
    this
  }

}

class MultiPartPostRequest(client: HttpClient, url: String) extends MultiPartRequest with MultiPartRequestBuilder {

  def execute(): Response = {
    val httpPost = new HttpPost(url)
    val reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE)

    for (part <- parts) {
      part match {
        case StringPart(k, v) => reqEntity.addPart(k, new StringBody(v))
        case FilePart(k, v) => reqEntity.addPart(k, new FileBody(v))
      }
    }

    httpPost.setEntity(reqEntity)

    new Response(client.execute(httpPost))
  }

}
