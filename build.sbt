
ThisBuild / scalaVersion := "2.13.6"
ThisBuild / organization := "com.example"

lazy val hello = (project in file("."))
  .settings(
    name := "Final project"
  )

addCompilerPlugin(
  "org.typelevel" %% "kind-projector" % "0.13.2" cross CrossVersion.full
)

libraryDependencies += "org.typelevel" %% "cats-core" % "2.7.0"
libraryDependencies += "org.typelevel" %% "cats-laws" % "2.7.0"
libraryDependencies += "org.typelevel" %% "discipline-core" % "1.5.1"
libraryDependencies += "org.typelevel" %% "discipline-scalatest" % "2.1.5"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.12"
