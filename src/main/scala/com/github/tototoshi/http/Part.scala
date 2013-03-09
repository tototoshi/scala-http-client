package com.github.tototoshi.http

import java.io.File

sealed trait Part[A]
case class StringPart(key: String, value: String) extends Part[String]
case class FilePart(key: String, value: File) extends Part[File]

