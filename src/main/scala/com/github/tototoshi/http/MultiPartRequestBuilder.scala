package com.github.tototoshi.http

trait MultiPartRequestBuilder { self: MultiPartRequest =>

  def part[A](key: String, value: A)(implicit converter: PartConverter[A]): this.type = {
    parts :+= converter.convert(key, value)
    this
  }

}

