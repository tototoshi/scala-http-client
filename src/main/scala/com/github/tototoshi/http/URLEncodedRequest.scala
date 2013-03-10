/*
 * Copyright 2013 Toshiyuki Takahashi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.tototoshi.http

import org.apache.http.client.methods.{ HttpGet, HttpPost, HttpUriRequest }
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.client.HttpClient
import org.apache.http.client.utils.URIBuilder

private[http] class UrlEncodedGetRequest(client: HttpClient, url: String)
    extends RequestBuilder
    with Request {

  def execute(): Response = {
    val uriBuilder = new URIBuilder(url)
    params.foreach { case (name, value) => uriBuilder.setParameter(name, value) }
    val request = new HttpGet(uriBuilder.build)
    headers.foreach { case (k, v) => request.addHeader(k, v) }
    new Response(client.execute(request))
  }

}

private[http] class UrlEncodedPostRequest(client: HttpClient, url: String)
    extends RequestBuilder
    with Request {

  def execute(): Response = {
    val request = new HttpPost(url)
    headers.foreach { case (k, v) => request.addHeader(k, v) }
    request.setEntity(new UrlEncodedFormEntity(constructNameValuePairs(params), _encoding))
    new Response(client.execute(request))
  }

}

