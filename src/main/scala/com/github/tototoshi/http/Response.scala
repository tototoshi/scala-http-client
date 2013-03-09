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
