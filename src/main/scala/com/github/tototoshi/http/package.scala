package com.github.tototoshi

import java.io.File

package object http {

  implicit val stringPartConverter = new PartConverter[String] {
    def convert(key: String, value: String) = StringPart(key, value)
  }

  implicit val filePartConverter = new PartConverter[File] {
    def convert(key: String, value: File) = FilePart(key, value)
  }

}

