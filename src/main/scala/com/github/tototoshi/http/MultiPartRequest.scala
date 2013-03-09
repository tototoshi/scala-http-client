package com.github.tototoshi.http

trait MultiPartRequest extends Request {

  var parts: Seq[Part[_]] = Seq()

}

