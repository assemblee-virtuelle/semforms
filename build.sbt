organization := "virtual-assembly"

name := "semantic_forms_play"

version := "1.0-SNAPSHOT"

lazy val semantic_forms =  RootProject(file("../forms"))

lazy val semantic_forms_play = (project in file("."))
        .enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies += "deductions" %% "semantic_forms" % "1.0-SNAPSHOT"

libraryDependencies += "deductions" %% "semantic_forms_play" % "1.0-SNAPSHOT"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.0" % Test


javacOptions ++= Seq("-source","1.7", "-target","1.7")

resolvers += Resolver.file("Local repo", file(System.getProperty("user.home") + "/.ivy2/local"))(Resolver.ivyStylePatterns)


