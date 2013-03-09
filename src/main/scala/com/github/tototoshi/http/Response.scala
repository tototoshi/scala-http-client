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

import org.apache.http.HttpResponse
import org.apache.http.util.EntityUtils
import java.io.File
import org.json4s._
import org.json4s.native.JsonMethods._

class Response(httpResponse: HttpResponse) extends Using {
  def statusCode(): Int = {
    EntityUtils.consume(httpResponse.getEntity)
    httpResponse.getStatusLine.getStatusCode
  }
  def asString(): String = asString("UTF-8")
  def asString(charset: String): String = {
    val res = EntityUtils.toString(httpResponse.getEntity, charset)
    EntityUtils.consume(httpResponse.getEntity)
    res
  }
  def asJson(): JValue = parse(asString)
  def save(filename: String): Unit = save(new File(filename))
  def save(file: File): Unit = {
    using(new java.io.BufferedInputStream(httpResponse.getEntity.getContent)) { in =>
      using(new java.io.PrintStream(file)) { out =>
        val buffer = new Array[Byte](8192)
        Stream.continually(in.read(buffer)).takeWhile(_ >= 0) foreach {
          out.write(buffer, 0, _)
        }
      }
    }
  }
}
