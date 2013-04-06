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

case class Request[M <: Method, T <: ContentType](
    client: HttpClient,
    url: String,
    params: Map[String, Seq[String]] = Map.empty,
    parts: Seq[Part[_]] = Seq.empty,
    headers: Map[String, String] = Map.empty,
    encoding: String = "utf-8") {

  def body[A](p: (String, A))(implicit e: M =:= POST, converter: PartConverter[A]): Request[POST, T] = {
    val (key, value) = p
    this.copy(parts = parts :+ converter.convert(key, value))
  }

  def params(p: (String, String)*): Request[M, T] = {
    this.copy(params = p.foldLeft(params) {
      case (result, (key, value)) =>
        result + (key -> (result.getOrElse(key, Seq.empty[String]) :+ value))
    })
  }

  def header(h: (String, String)): Request[M, T] = {
    val (key, value) = h
    this.copy(headers = headers + (key -> value))
  }

  def encoding(e: String): Request[M, T] = {
    this.copy(encoding = encoding)
  }

  def execute()(implicit executor: RequestExecutor[M, T]): Response = {
    executor.execute(this)
  }

}
