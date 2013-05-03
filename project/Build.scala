import sbt._
import Keys._

object Settings {
  val buildOrganization = "templemore"
  val buildScalaVersion = "2.9.2"
  val buildVersion      = "0.7.3"

  val buildSettings = Defaults.defaultSettings ++
                      Seq (organization  := buildOrganization,
                           scalaVersion  := buildScalaVersion,
                           version       := buildVersion,
                           scalacOptions += "-deprecation",
                           publishTo := Some(Resolver.file("file",  new File("deploy-repo"))))
}

object Dependencies {

  private val CucumberVersionForScala2_9 = "1.0.9"
  private val CucumberVersionForScala2_10 = "1.1.3"

  def cucumberScala(scalaVersion: String) = {
    val cucumberVersion =
      if (scalaVersion.startsWith("2.10"))
        CucumberVersionForScala2_10
      else
        CucumberVersionForScala2_9

    "info.cukes" % "cucumber-scala" % cucumberVersion % "compile"
  }
  val cucumber = "info.cukes" % "cucumber-scala" % "1.0.9" % "compile"

  val testInterface = "org.scala-tools.testing" % "test-interface" % "0.5" % "compile"
}

object Build extends Build {
  import Dependencies._
  import Settings._

  lazy val parentProject = Project("sbt-cucumber-parent", file ("."),
    settings = buildSettings ++
               Seq(crossScalaVersions := Seq("2.9.2", "2.10.0"))) aggregate (pluginProject, integrationProject)

  lazy val pluginProject = Project("sbt-cucumber-plugin", file ("plugin"),
    settings = buildSettings ++ 
               Seq(sbtPlugin := true))

  lazy val integrationProject = Project ("sbt-cucumber-integration", file ("integration"),
    settings = buildSettings ++ 
               Seq(crossScalaVersions := Seq("2.9.2", "2.10.0"),
                   libraryDependencies <+= scalaVersion { sv => cucumberScala(sv) },
                   libraryDependencies += testInterface))
}

