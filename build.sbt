ThisBuild / version := "2023.3.0"

val scala2Version = "2.13.10"

lazy val root = project
  .in(file("."))
  .settings(
    organization := "io.github.todokr",
    name         := "sugarfix",
    description  := "Say goodbye to test fixtures that can no longer be fixed.",
    scalaVersion := scala2Version,
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-unchecked",
      "-Ytasty-reader"
    ),
    libraryDependencies ++= Seq(
      ("io.github.todokr" %% "grokschema" % "0.1.1").cross(CrossVersion.for2_13Use3),
      "com.lihaoyi"      %% "fastparse"  % "3.0.1",
      "org.scalameta"    %% "munit"      % "0.7.29" % Test
    )
  )
