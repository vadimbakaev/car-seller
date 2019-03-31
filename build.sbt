name := """car-seller"""
organization := "bakaev.vad"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"

libraryDependencies ++= {
  object Version {
    val scalaTest          = "4.0.1"
    val playJson           = "2.7.2"
    val mongo              = "2.6.0"
    val mockitoScala       = "1.2.2"
    val enumeratum         = "1.5.13"
    val enumeratumPlayJson = "1.5.16"
  }

  Seq(
    ws,
    guice,
    "com.beachape"           %% "enumeratum-play-json" % Version.enumeratumPlayJson,
    "com.beachape"           %% "enumeratum"           % Version.enumeratum,
    "org.mongodb.scala"      %% "mongo-scala-driver"   % Version.mongo,
    "com.typesafe.play"      %% "play-json"            % Version.playJson,
    "org.mockito"            %% "mockito-scala"        % Version.mockitoScala % Test,
    "org.scalatestplus.play" %% "scalatestplus-play"   % Version.scalaTest % Test
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
