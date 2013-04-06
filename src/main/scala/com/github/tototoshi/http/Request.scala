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
import java.util.{ ArrayList, List => JList }
import org.apache.http.message.BasicNameValuePair
import org.apache.http.NameValuePair

case class Request[M <: Method, T <: ContentType](
    client: HttpClient,
    url: String,
    parts: Seq[Part[_]] = Seq.empty,
    headers: Map[String, String] = Map.empty,
    params: Map[String, Seq[String]] = Map.empty,
    encoding: String = "utf-8") {

  def part[A](key: String, value: A)(implicit converter: PartConverter[A]): Request[POST, MultipartFormData] = {
    this.copy(parts = parts :+ converter.convert(key, value))
  }

  def param(key: String, value: String): Request[M, T] = {
    this.copy(params = params + (key -> (params.getOrElse(key, Seq.empty[String]) :+ value)))
  }

  def header(key: String, value: String): Request[M, T] = {
    this.copy(headers = headers + (key -> value))
  }

  def encoding(e: String): Request[M, T] = {
    this.copy(encoding = encoding)
  }

  protected def constructNameValuePairs(data: Iterable[(String, String)]): JList[NameValuePair] = {
    data.foldLeft(new ArrayList[NameValuePair](data.size)) {
      case (pairs, (k, v)) => { pairs.add(new BasicNameValuePair(k, v)); pairs }
    }
  }

  def execute()(implicit executor: RequestExecutor[M, T]): Response = {
    executor.execute(this)
  }

}
