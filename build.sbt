name := """car-seller"""
organization := "bakaev.vad"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"

libraryDependencies ++= {
  object Version {
    val scalaTest = "4.0.1"
    val playJson  = "2.7.2"
    val mongo     = "2.6.0"
  }

  Seq(
    ws,
    guice,
    "org.mongodb.scala"      %% "mongo-scala-driver" % Version.mongo,
    "com.typesafe.play"      %% "play-json"          % Version.playJson,
    "org.scalatestplus.play" %% "scalatestplus-play" % Version.scalaTest % Test
  )
}

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-Ypartial-unification",
  "-Ywarn-inaccessible",
  "-Ywarn-infer-any",
  "-Ywarn-nullary-override",
  "-Ywarn-nullary-unit",
  "-Xfatal-warnings"
)

coverageEnabled := true
scalafmtOnCompile := true
parallelExecution in Test := true
coverageExcludedPackages := "<empty>;controllers.javascript;router;models;view.*;config.*;.*(AuthService|BuildInfo|Routes).*"
