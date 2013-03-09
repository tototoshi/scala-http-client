package com.github.tototoshi.http

import org.apache.http.client.methods.{ HttpGet, HttpPost, HttpUriRequest }
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.client.HttpClient

class UrlEncodedGetRequest(client: HttpClient, url: String)
    extends RequestBuilder
    with Request {

  def execute(): Response = {
    val urlWithParams = if (params.isEmpty) url else url + "?" +
      URLEncodedUtils.format(constructNameValuePairs(params), _encoding)
    val request = new HttpGet(urlWithParams)
    headers.foreach { case (k, v) => request.addHeader(k, v) }
    new Response(client.execute(request))
  }

}

class UrlEncodedPostRequest(client: HttpClient, url: String)
    extends RequestBuilder
    with Request {

  def execute(): Response = {
    val request = new HttpPost(url)
    headers.foreach { case (k, v) => request.addHeader(k, v) }
    request.setEntity(new UrlEncodedFormEntity(constructNameValuePairs(params), _encoding))
    new Response(client.execute(request))
  }

}

