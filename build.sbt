name := "ivory-config"

organization := "pl.mietkins"

version := "0.1"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "com.typesafe" % "config" % "1.3.0",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test")