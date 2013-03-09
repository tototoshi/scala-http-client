package com.github.tototoshi.http

trait PartConverter[A] {
  def convert(key: String, value: A): Part[A]
}

