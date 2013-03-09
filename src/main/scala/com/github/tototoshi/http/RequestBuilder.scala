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

import java.util.{ ArrayList, List => JList }
import org.apache.http.message.BasicNameValuePair
import org.apache.http.NameValuePair

trait RequestBuilder { self: Request =>

  def param(key: String, value: String): this.type = {
    params += (key -> value)
    this
  }

  def header(key: String, value: String): this.type = {
    headers += (key -> value)
    this
  }

  def encoding(e: String): this.type = {
    _encoding = e
    this
  }

  protected def constructNameValuePairs(data: Iterable[(String, String)]): JList[NameValuePair] = {
    data.foldLeft(new ArrayList[NameValuePair](data.size)) {
      case (pairs, (k, v)) => { pairs.add(new BasicNameValuePair(k, v)); pairs }
    }
  }

}

