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

import org.scalatest.FunSpec
import org.scalatest.matchers._
import scala.io.Source
import java.io.File
import unfiltered.filter._
import unfiltered.filter.request._
import unfiltered.request.{ GET => UGET, POST => UPOST, _ }
import unfiltered.response._

class ClientSpec extends FunSpec
    with ShouldMatchers
    with MockServer {

  def localhost(port: Int): String = "http://localhost:" + port

  val plan = Planify {
    case UGET(Path("/foo")) => { ResponseString("foo") }
    case UPOST(Path("/bar")) => { ResponseString("bar") }
    case UGET(Path("/params")) & Params(params) => { ResponseString(params("a").mkString(",")) }
    case UPOST(Path("/params")) & Params(params) => { ResponseString(params("a").mkString(",")) }
    case UPOST(Path("/upload")) & MultiPart(req) => {
      val MultipartData(params, files) = MultiPartParams.Memory(req)
      val fileContent = new String(files("file").head.bytes, "utf-8").trim
      ResponseString(fileContent + ":" + params("a").head)
    }
  }

  describe("Client") {

    it("should get") {
      withMockServer(plan) { port =>
        new Client()
          .get(localhost(port) + "/foo")
          .execute.asString should be("foo")
      }
    }

    it("should get with params") {
      withMockServer(plan) { port =>
        new Client()
          .get(localhost(port) + "/params")
          .params("a" -> "b")
          .execute
          .asString should be("b")
      }
    }

    it("should post") {
      withMockServer(plan) { port =>
        new Client()
          .post(localhost(port) + "/bar")
          .execute.asString should be("bar")
      }
    }

    it("should post with params") {
      withMockServer(plan) { port =>
        new Client()
          .post(localhost(port) + "/params")
          .params("a" -> "b")
          .execute.asString should be("b")
      }
    }

    it("should post multipart request") {
      withMockServer(plan) { port =>
        new Client()
          .post(localhost(port) + "/upload")
          .part("a" -> "b")
          .part("file", new File("src/test/resources/upload-test.txt"))
          .execute
          .asString should be("chakapoko chakapoko:b")
      }
    }

  }
}
