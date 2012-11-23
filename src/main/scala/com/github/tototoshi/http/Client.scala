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
import org.apache.http.{ NameValuePair, HttpEntity }

class Client {
  private def httpClient = {
    val c = new DefaultHttpClient()
    val routePlanner = new ProxySelectorRoutePlanner(
      c.getConnectionManager.getSchemeRegistry, ProxySelector.getDefault)
    c.setRoutePlanner(routePlanner)
    c
  }

  private def constructNameValuePairs(data: Iterable[(String, String)]): JList[NameValuePair] = {
    data.foldLeft(new ArrayList[NameValuePair](data.size)) {
      case (pairs, (k, v)) => { pairs.add(new BasicNameValuePair(k, v)); pairs }
    }
  }

  def GET(url: String, param: Iterable[(String, String)] = Map(), header: Map[String, String] = Map(), encoding: String = "UTF-8"): Response = {
    val urlWithParams = if (param.isEmpty) url else url + "?" + URLEncodedUtils.format(constructNameValuePairs(param), encoding)
    val request = new HttpGet(urlWithParams)
    header foreach { case (k, v) => request.addHeader(k, v) }
    new Response(httpClient.execute(request))
  }

  def POST(url: String, params: Iterable[(String, String)] = Map(), header: Map[String, String] = Map(), encoding: String = "UTF-8"): Response = {
    val request = new HttpPost(url)
    header foreach { case (k, v) => request.addHeader(k, v) }
    request.setEntity(new UrlEncodedFormEntity(constructNameValuePairs(params), encoding))
    new Response(httpClient.execute(request))
  }

}


