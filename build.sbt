name := """hotel-view"""
organization := "org.gk"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion in ThisBuild := "2.11.11"

//libraryDependencies += filters
libraryDependencies += "mysql" % "mysql-connector-java" % "6.0.6"
libraryDependencies += "com.typesafe.play" %% "play-slick" % "2.0.0"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.5.15"