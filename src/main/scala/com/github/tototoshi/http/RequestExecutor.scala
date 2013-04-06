/*
 * Copyright 2013 Toshiyuki Takahaashi
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

import com.github.tototoshi.http.util.URIBuilder

trait RequestExecutor[M <: Method, T <: ContentType] {

  def execute(request: Request[M, T]): Response

}

trait GetRequestExecutor
    extends RequestExecutor[GET, NonType]
    with URIBuilder {

  import org.apache.http.client.methods.HttpGet

  def execute(request: Request[GET, NonType]): Response = {
    val httpRequest = new HttpGet(buildURI(request.url, request.params))
    request.headers.foreach { case (k, v) => httpRequest.addHeader(k, v) }
    new Response(request.client.execute(httpRequest))
  }

}

trait FormUrlEncodedExecutor
    extends RequestExecutor[POST, FormUrlEncoded]
    with URIBuilder {

  import org.apache.http.client.methods.HttpPost
  import org.apache.http.client.entity.UrlEncodedFormEntity
  import org.apache.http.client.HttpClient
  import java.util.{ ArrayList, List => JList }
  import org.apache.http.message.BasicNameValuePair
  import org.apache.http.NameValuePair

  protected def constructNameValuePairs(data: Seq[(String, String)]): JList[NameValuePair] = {
    data.foldLeft(new ArrayList[NameValuePair](data.size)) {
      case (pairs, (k, v)) => { pairs.add(new BasicNameValuePair(k, v)); pairs }
    }
  }

  def execute(request: Request[POST, FormUrlEncoded]): Response = {
    val httpRequest = new HttpPost(buildURI(request.url, request.params))
    request.headers.foreach { case (k, v) => httpRequest.addHeader(k, v) }
    val reqEntity = new UrlEncodedFormEntity(constructNameValuePairs(request.body.map { body => (body.key, body.value.toString) }))
    httpRequest.setEntity(reqEntity)
    new Response(request.client.execute(httpRequest))
  }

}

trait MulitiPartRequestExecutor
    extends RequestExecutor[POST, MultipartFormData]
    with URIBuilder {

  import org.apache.http.client.HttpClient
  import org.apache.http.client.methods.HttpPost
  import org.apache.http.entity.mime.{ MultipartEntity, HttpMultipartMode }
  import org.apache.http.entity.mime.content.{ StringBody => ApacheStringBody, FileBody => ApacheFileBody }

  def execute(request: Request[POST, MultipartFormData]): Response = {
    val httpRequest = new HttpPost(buildURI(request.url, request.params))
    val reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE)
    request.headers.foreach { case (k, v) => httpRequest.addHeader(k, v) }
    for (body <- request.body) {
      body match {
        case StringBody(k, v) => reqEntity.addPart(k, new ApacheStringBody(v))
        case FileBody(k, v) => reqEntity.addPart(k, new ApacheFileBody(v))
      }
    }
    httpRequest.setEntity(reqEntity)
    new Response(request.client.execute(httpRequest))
  }

}

