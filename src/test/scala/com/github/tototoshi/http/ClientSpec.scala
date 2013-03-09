package com.github.tototoshi.http

import org.scalatest.FunSpec
import org.scalatest.matchers._

import unfiltered.filter._
import unfiltered.request._
import unfiltered.response._

class ClientSpec extends FunSpec with ShouldMatchers with MockServer {

  val plan = Planify {
    case GET(Path("/foo")) => { ResponseString("foo") }
    case POST(Path("/bar")) => { ResponseString("bar") }
    case GET(Path("/params")) & Params(params) => { ResponseString(params("a").head) }
    case POST(Path("/params")) & Params(params) => { ResponseString(params("a").head) }
  }

  describe("Client") {

    it("should get") {
      withMockServer(plan) { port =>
        new Client().get("http://localhost:" + port + "/foo").execute.asString should be ("foo")
      }
    }

    it("should get with params") {
      withMockServer(plan) { port =>
        val req = new Client() .get("http://localhost:" + port + "/params") .param("a", "b")
        req.execute.asString should be ("b")
      }
    }

    it("should post") {
      withMockServer(plan) { port =>
        new Client().post("http://localhost:" + port + "/bar").execute.asString should be ("bar")
      }
    }

    it("should post with params") {
      withMockServer(plan) { port =>
        val req = new Client().post("http://localhost:" + port + "/params").param("a", "b")
        req.execute.asString should be ("b")
      }
    }

  }
}
