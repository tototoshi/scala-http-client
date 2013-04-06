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

trait RequestExecutor[M <: Method, T <: ContentType] {

  def execute(request: Request[M, T]): Response

}

trait GetRequestExecutor
    extends RequestExecutor[GET, NonType] {

  import org.apache.http.client.utils.URIBuilder
  import org.apache.http.client.methods.HttpGet

  def execute(request: Request[GET, NonType]): Response = {
    val uriBuilder = new URIBuilder(request.url)
    request.params.foreach { case (name, value) => uriBuilder.setParameter(name, value) }
    val httpRequest = new HttpGet(uriBuilder.build)
    request.headers.foreach { case (k, v) => httpRequest.addHeader(k, v) }
    new Response(request.client.execute(httpRequest))
  }

}

trait FormUrlEncodedExecutor extends RequestExecutor[POST, FormUrlEncoded] {

  import org.apache.http.client.utils.URIBuilder
  import org.apache.http.client.methods.HttpPost
  import org.apache.http.client.entity.UrlEncodedFormEntity
  import org.apache.http.client.HttpClient

  def execute(request: Request[POST, FormUrlEncoded]): Response = {
    val uriBuilder = new URIBuilder(request.url)
    request.params.foreach { case (name, value) => uriBuilder.setParameter(name, value) }
    val httpRequest = new HttpPost(uriBuilder.build)
    request.headers.foreach { case (k, v) => httpRequest.addHeader(k, v) }
    new Response(request.client.execute(httpRequest))
  }

}

trait MulitiPartRequestExecutor extends RequestExecutor[POST, MultipartFormData] {

  import org.apache.http.client.HttpClient
  import org.apache.http.client.methods.HttpPost
  import org.apache.http.client.utils.URIBuilder
  import org.apache.http.entity.mime.{ MultipartEntity, HttpMultipartMode }
  import org.apache.http.entity.mime.content.{ StringBody, FileBody }

  def execute(request: Request[POST, MultipartFormData]): Response = {

    val url2 = {
      val uriBuilder = new URIBuilder(request.url)
      request.params.foreach { case (name, value) => uriBuilder.setParameter(name, value) }
      uriBuilder.build
    }

    val httpRequest = new HttpPost(url2)
    val reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE)

    request.headers.foreach { case (k, v) => httpRequest.addHeader(k, v) }

    for (part <- request.parts) {
      part match {
        case StringPart(k, v) => reqEntity.addPart(k, new StringBody(v))
        case FilePart(k, v) => reqEntity.addPart(k, new FileBody(v))
      }
    }

    httpRequest.setEntity(reqEntity)

    new Response(request.client.execute(httpRequest))
  }

}

