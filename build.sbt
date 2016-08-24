import sbt.Keys._
import sbt._

name := "ivory-config"

organization := "pl.mietkins"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.8"

publishMavenStyle := true

publishArtifact in Test := false

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "com.typesafe" % "config" % "1.3.0",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test")

licenses := Seq(
  "The Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

homepage := Some(url("https://github.com/mietkins/"))

scmInfo := Some(ScmInfo(
  url("https://github.com/mietkins/ivory-config.git"),
  "scm:git:git@github.com:mietkins/ivory-config.git"))

developers := List(Developer(
  "mietkins",
  "Marcin Antczak",
  "mietkins7@gmail.com",
  url("https://github.com/mietkins/ivory-config")))