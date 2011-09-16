package com.github.tototoshi.http

import java.io.{BufferedReader, InputStream, InputStreamReader}
import java.net.ProxySelector
import java.util.ArrayList
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.{HttpGet, HttpPost}
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.impl.conn.ProxySelectorRoutePlanner
import org.apache.http.message.BasicNameValuePair
import org.apache.http.{HttpResponse, NameValuePair, HttpEntity}

trait Using {
  type Closable = {def close():Unit}
  def using[A <: Closable,B](resource:A)(f:A => B) = {
    try {
      f(resource)
    } finally {
      resource.close
    }
  }
}

class Client extends Using{
  val httpClient = new DefaultHttpClient()
  val routePlanner = new ProxySelectorRoutePlanner(
    httpClient.getConnectionManager.getSchemeRegistry, ProxySelector.getDefault
  )
  httpClient.setRoutePlanner(routePlanner)

  private def constructNameValuePairs(data: Map[String, String]): ArrayList[NameValuePair] = {
    data.foldLeft(new ArrayList[NameValuePair](data.size)) {
      case (pairs, (k, v)) => { pairs.add(new BasicNameValuePair(k, v)); pairs }
   }
  }

  def GET(url: String, param: Map[String, String] = Map(), encoding: String = "UTF-8"): Response = {
    val urlWithParams = if (param.isEmpty) url else url + "?" + URLEncodedUtils.format(constructNameValuePairs(param), encoding)
    val request = new HttpGet(urlWithParams)
    new Response(httpClient.execute(request))
  }

  def POST(url: String, params: Map[String, String] = Map()): Response = {
    val request = new HttpPost(url)
    request.setEntity(new UrlEncodedFormEntity(constructNameValuePairs(params)))
    new Response(httpClient.execute(request))
  }

  class Response(httpResponse: HttpResponse) {
    def statusCode(): Int = {
      using(httpResponse.getEntity.getContent) { in =>
        httpResponse.getStatusLine.getStatusCode
      }
    }
    def asString(): String = asString("UTF-8")
    def asString(charset: String): String = {
      using(httpResponse.getEntity.getContent) { in =>
        val builder = new StringBuilder
        val br = new BufferedReader(new InputStreamReader(in, charset))
        Stream.continually(br.readLine).takeWhile(_ != null).foldLeft(builder){_.append(_)}.toString
      }
    }
  }
}
