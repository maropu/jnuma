import sbt._
import sbt.Keys._

object Build extends sbt.Build {

  lazy val jnuma = Project(
    id = "jnuma",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "jnuma",
      organization := "xerial",
      version := "0.2.0",
      scalaVersion := "2.10.4"
      // add other settings here
    )
  )
}
