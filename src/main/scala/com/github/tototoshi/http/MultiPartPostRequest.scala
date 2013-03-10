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

import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.utils.URIBuilder
import org.apache.http.entity.mime.{ MultipartEntity, HttpMultipartMode }
import org.apache.http.entity.mime.content.{ StringBody, FileBody }

private[http] class MultiPartPostRequest(client: HttpClient, url: String)
    extends MultiPartRequestBuilder {

  def execute(): Response = {
    val uriBuilder = new URIBuilder(url)
    params.foreach { case (name, value) => uriBuilder.setParameter(name, value) }

    val request = new HttpPost(uriBuilder.build)
    val reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE)

    headers.foreach { case (k, v) => request.addHeader(k, v) }

    for (part <- parts) {
      part match {
        case StringPart(k, v) => reqEntity.addPart(k, new StringBody(v))
        case FilePart(k, v) => reqEntity.addPart(k, new FileBody(v))
      }
    }

    request.setEntity(reqEntity)

    new Response(client.execute(request))
  }

}
