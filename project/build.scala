import sbt._
import Keys._

object build extends Build {
  val Organization = "info.carueda"
  val Name = "jnanorest"
  val Version = "0.1.0"
  val ScalaVersion = "2.10.2"

  lazy val project = Project (
    "jnanorest",
    file("."),
    settings = Defaults.defaultSettings ++ Seq(
      organization := Organization,
      name := Name,
      version := Version,
      scalaVersion := ScalaVersion,
      resolvers += Classpaths.typesafeReleases,
      libraryDependencies ++= Seq(
        "com.google.code.gson"        % "gson"                 % "2.2.4"
      )
    )
  )
}

