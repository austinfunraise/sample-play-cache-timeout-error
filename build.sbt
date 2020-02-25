name := """play-cache-test"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  // issue also appears with ehcache instead of caffeine
  caffeine,
  guice
)
