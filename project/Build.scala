import sbt._
import Keys._

object ScalaHttpClientBuild extends Build {

  val unfilteredVersion = "0.6.7"
  val httpcomponentsVersion = "4.2.2"
  val json4sVersion = "3.1.0"

  lazy val root = Project (
    id = "scala-http-client",
    base = file ("."),
    settings = Defaults.defaultSettings ++ Seq (
      name := "scala-http-client",
      version := "1.0",
      organization := "com.github.tototoshi",
      scalaVersion := "2.10.0",
      scalacOptions ++= Seq("-feature"),
      libraryDependencies ++= Seq(
        "org.apache.httpcomponents" % "httpclient" % httpcomponentsVersion,
        "org.apache.httpcomponents" % "httpmime" % httpcomponentsVersion,
        "org.json4s" %% "json4s-native" % json4sVersion,
        "org.scalatest" %% "scalatest" % "1.9.1" % "test",
        "net.databinder" %% "unfiltered-filter" % unfilteredVersion % "test",
        "net.databinder" %% "unfiltered-jetty" % unfilteredVersion % "test"
      )
    )
  )
}

