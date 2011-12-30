package com.github.tototoshi.http

import java.io.{ File, BufferedReader, InputStream, InputStreamReader }
import java.net.ProxySelector
import java.util.{ ArrayList, List => JList }
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.{ HttpGet, HttpPost, HttpUriRequest }
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.impl.conn.ProxySelectorRoutePlanner
import org.apache.http.message.BasicNameValuePair
import org.apache.http.{ HttpResponse, NameValuePair, HttpEntity }
import org.apache.http.util.EntityUtils

trait Using {
  type Closable = { def close(): Unit }
  def using[A <: Closable, B](resource: A)(f: A => B) = {
    try {
      f(resource)
    } finally {
      resource.close
    }
  }
}

class Client extends Using {
  val httpClient = new DefaultHttpClient()
  val routePlanner = new ProxySelectorRoutePlanner(
    httpClient.getConnectionManager.getSchemeRegistry, ProxySelector.getDefault)
  httpClient.setRoutePlanner(routePlanner)

  private def constructNameValuePairs(data: Map[String, String]): JList[NameValuePair] = {
    data.foldLeft(new ArrayList[NameValuePair](data.size)) {
      case (pairs, (k, v)) => { pairs.add(new BasicNameValuePair(k, v)); pairs }
    }
  }

  def GET(url: String, param: Map[String, String] = Map(), header: Map[String, String] = Map(), encoding: String = "UTF-8"): Response = {
    val urlWithParams = if (param.isEmpty) url else url + "?" + URLEncodedUtils.format(constructNameValuePairs(param), encoding)
    val request = new HttpGet(urlWithParams)
    header foreach { case (k, v) => request.addHeader(k, v) }
    new Response(httpClient.execute(request))
  }

  def POST(url: String, params: Map[String, String] = Map(), header: Map[String, String] = Map()): Response = {
    val request = new HttpPost(url)
    header foreach { case (k, v) => request.addHeader(k, v) }
    request.setEntity(new UrlEncodedFormEntity(constructNameValuePairs(params)))
    new Response(httpClient.execute(request))
  }

  class Response(httpResponse: HttpResponse) {
    def statusCode(): Int = {
      EntityUtils.consume(httpResponse.getEntity)
      httpResponse.getStatusLine.getStatusCode
    }
    def asString(): String = asString("UTF-8")
    def asString(charset: String): String = {
      EntityUtils.toString(httpResponse.getEntity, charset)
    }
    def save(filename: String): Unit = save(new File(filename))
    def save(file: File): Unit = {
      using(new java.io.BufferedInputStream(httpResponse.getEntity.getContent)) { in =>
        using(new java.io.PrintStream(file)) { out =>
          val buffer = new Array[Byte](8192)
          Stream.continually(in.read(buffer)).takeWhile(_ >= 0) foreach {
            out.write(buffer, 0, _)
          }
        }
      }
    }
  }

}
