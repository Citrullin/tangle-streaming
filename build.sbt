name := "iri-stream-provider"

version := "0.0.1"

scalaVersion := "2.11.11"

libraryDependencies += "org.zeromq" % "jeromq" % "0.4.3"
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.25"
libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.25"
libraryDependencies += "com.thesamet.scalapb" %% "scalapb-json4s" % "0.7.0"

organization in ThisBuild := "com.gameole"

PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)