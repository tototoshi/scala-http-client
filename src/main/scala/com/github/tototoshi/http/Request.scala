package com.github.tototoshi.http

import java.io.File
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils
import org.apache.http.{ NameValuePair, HttpEntity, HttpRequest }

trait Request {

  protected var headers: Map[String, String] = Map.empty

  protected var params: Map[String, String] = Map.empty

  protected var _encoding: String = "utf-8"

  def execute(): Response

}

