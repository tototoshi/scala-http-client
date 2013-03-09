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

