
ThisBuild / scalaVersion := "2.13.6"
ThisBuild / organization := "com.example"

lazy val hello = (project in file("."))
  .settings(
    name := "Some Fp project"
  )

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-feature",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:existentials",
  "-language:postfixOps",
  "-unchecked",
  "-Ywarn-value-discard"
  //    "-Wconf:cat=other-match-analysis:error", // uncomment to transform non-exhaustive warnings into errors
  //    "-Wconf:cat=unchecked:error",            // uncomment to transform type erasure warnings into errors
)

addCompilerPlugin(
  "org.typelevel" %% "kind-projector" % "0.10.3" cross CrossVersion.binary
)

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.7.0",
  "org.typelevel" %% "cats-laws" % "2.7.0",
  "org.typelevel" %% "discipline-core" % "1.4.0" % Test,
  "org.typelevel" %% "discipline-scalatest" % "2.1.5" % Test,
  "org.scalatest" %% "scalatest" % "3.2.11" % Test
)
