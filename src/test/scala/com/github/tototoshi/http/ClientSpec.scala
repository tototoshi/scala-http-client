package com.github.tototoshi.http

import org.specs2._
import scala.io.Source
import java.io.File
import net.liftweb.json.JsonAST._

class ClientSpec extends Specification { def is =

  sequential ^
  "Client.GET get"                                                         ^
    "200 as a status code"                                                 ! statuscode ^
    "'It is a truth...'"                                                   ! resultString ^
    "client.GET(url).save(filename) save the contents to filename"         ! save1 ^
    "get response as json"                                                 ! getJson ^
                                                                           p^
  "Client.POST get"                                                        ^
    "200 as a status code"                                                 ! statuscode2 ^
    "'It is a truth...'"                                                   ! resultString2 ^
    "client.POST(url).save(filename) save the contents to filename"        ! save2 ^ end

  val jane = "It is a truth universally acknowledged, that a single man in possession of a good fortune, must be in want of a wife.\n"
  val testserver = "http://technically.us/test.text"

  val client = new Client
  def statuscode = client.GET(testserver).statusCode must be equalTo(200)
  def statuscode2 = client.POST(testserver).statusCode must be equalTo(200)
  def resultString = client.GET(testserver).asString must be equalTo(jane)
  def resultString2 = client.POST(testserver).asString must be equalTo(jane)
  def save1 = {
    val file = new File("7fb9b160-d703-47d3-a459-621b80e48b1d")
    client.POST(testserver).save(file)
    val contents = Source.fromFile(file).getLines.mkString("\n")
    file.delete()
    contents must be equalTo(jane.substring(0, jane.length - 1))
  }
  def save2 = {
    val file = new File("c95a694d-b240-4ae5-a92f-e50b32e238f1")
    client.GET(testserver).save(file)
    val contents = Source.fromFile(file).getLines.mkString
    file.delete()
    contents must be equalTo(jane.substring(0, jane.length - 1))
  }

  def getJson = {
    val JArray(items) = client.GET("https://api.github.com/gists") asJson
    val gitPushUrl = (for {
      JObject(item) <- items
      JField("git_push_url", JString(value)) <- item
    } yield value).head
    gitPushUrl must be startWith "git@gist.github.com"
  }

}
