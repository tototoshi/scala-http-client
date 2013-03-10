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

  def get(url: String): RequestBuilder = new UrlEncodedGetRequest(_client, url)

  def post(url: String): RequestBuilder = new UrlEncodedPostRequest(_client, url)

  def postMultiPart(url: String): MultiPartRequestBuilder = new MultiPartPostRequest(_client, url)

}
