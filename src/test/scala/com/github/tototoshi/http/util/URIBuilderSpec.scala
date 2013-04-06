package com.github.tototoshi.http.util

import org.scalatest.FunSpec
import org.scalatest.matchers._

class URIBuilderSpec extends FunSpec with ShouldMatchers {

  val builder = new URIBuilder {}

  describe("URIBuilder") {

    it("should build uri") {
      import java.net.URI
      val uri = builder.buildURI("http://www.google.co.jp", Map("q" -> Seq("foo", "bar"), "hl" -> Seq("ja")))
      uri should be(new URI("http://www.google.co.jp?q=foo&q=bar&hl=ja"))
    }

  }
}
