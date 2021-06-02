scalaVersion := "2.13.3"

name := "command"
organization := "io.typecraft"
version := "0.1.0"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-kernel" % "2.3.0",
  "org.typelevel" %% "cats-effect-kernel" % "3.1.1",
  "org.scalatest" %% "scalatest" % "3.0.8" % Test,
  "org.typelevel" %% "cats-core" % "2.3.0" % Test,
  "org.typelevel" %% "cats-effect" % "3.1.1" % Test,
)
