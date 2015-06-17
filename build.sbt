organization := "virtual-assembly"

name := "semantic_forms"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.6"

javacOptions ++= Seq("-source","1.7", "-target","1.7")


libraryDependencies += "deductions" %% "semantic_forms" % "1.0-SNAPSHOT"

