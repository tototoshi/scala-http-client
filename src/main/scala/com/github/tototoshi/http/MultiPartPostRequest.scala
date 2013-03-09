package com.github.tototoshi.http

import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.mime.{ MultipartEntity, HttpMultipartMode }
import org.apache.http.entity.mime.content.{ StringBody, FileBody }

class MultiPartPostRequest(client: HttpClient, url: String)
    extends MultiPartRequest
    with MultiPartRequestBuilder {

  def execute(): Response = {
    val httpPost = new HttpPost(url)
    val reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE)

    for (part <- parts) {
      part match {
        case StringPart(k, v) => reqEntity.addPart(k, new StringBody(v))
        case FilePart(k, v) => reqEntity.addPart(k, new FileBody(v))
      }
    }

    httpPost.setEntity(reqEntity)

    new Response(client.execute(httpPost))
  }

}
