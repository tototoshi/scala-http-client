name := "scala-http-client"

version := "1.0"

organization := "com.github.tototoshi"

scalaVersion := "2.9.1"

libraryDependencies ++= Seq(
  "org.apache.httpcomponents" % "httpclient" % "4.1.2",
  "net.liftweb" %% "lift-json" % "2.4",
  "org.specs2" %% "specs2" % "1.6.1" % "test",
  "org.specs2" %% "specs2-scalaz-core" % "6.0.1" % "test"
)


