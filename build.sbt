name := "iri-stream-provider"

version := "0.0.1"

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  "org.zeromq" % "jeromq" % "0.4.3",
  "org.slf4j" % "slf4j-api" % "1.7.25",
  "org.slf4j" % "slf4j-simple" % "1.7.25",
  "com.thesamet.scalapb" %% "scalapb-json4s" % "0.7.0",
  "org.specs2" %% "specs2-core" % "4.0.2" % "test",
  "org.apache.logging.log4j" %% "log4j-api-scala" % "11.0",
  "org.apache.logging.log4j" % "log4j-api" % "2.8.2",
  "org.apache.logging.log4j" % "log4j-core" % "2.8.2" % Runtime
)

scalacOptions in Test ++= Seq("-Yrangepos")

organization in ThisBuild := "com.gameole"

PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)