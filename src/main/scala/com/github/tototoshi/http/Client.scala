package com.github.tototoshi.http

import java.net.ProxySelector
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.impl.conn.ProxySelectorRoutePlanner
import org.apache.http.NameValuePair

class Client {

  private def _client = {
    val c = new DefaultHttpClient()
    val routePlanner = new ProxySelectorRoutePlanner(
      c.getConnectionManager.getSchemeRegistry, ProxySelector.getDefault)
    c.setRoutePlanner(routePlanner)
    c
  }

  def get(url: String): Request = new UrlEncodedGetRequest(_client, url)

  def post(url: String): Request = new UrlEncodedPostRequest(_client, url)

  def postMultiPart(url: String): MultiPartPostRequest = new MultiPartPostRequest(_client, url)

}
