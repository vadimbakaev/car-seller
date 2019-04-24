name := """car-seller"""
organization := "bakaev.vad"

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)

scalaSource in IntegrationTest := baseDirectory.value / "/it"

scalaVersion := "2.12.8"

libraryDependencies ++= {
  object Version {
    val scalaTest          = "4.0.2"
    val playJson           = "2.7.3"
    val mongo              = "2.6.0"
    val mockitoScala       = "1.3.1"
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
    "org.mockito"            %% "mockito-scala"        % Version.mockitoScala % "it, test",
    "org.scalatestplus.play" %% "scalatestplus-play"   % Version.scalaTest % "it, test"
  )
}

scalacOptions ++= Seq(
  "-feature",
  "-unchecked",
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-Ypartial-unification",
  "-Ywarn-inaccessible",
  "-Ywarn-infer-any",
  "-Ywarn-nullary-override",
  "-Ywarn-nullary-unit",
  "-Xfatal-warnings",
  "-Yno-adapted-args", // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
  "-Ypartial-unification", // Enable partial unification in type constructor inference
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-extra-implicit", // Warn when more than one implicit parameter section is defined.
  "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
  "-Ywarn-infer-any", // Warn when a type argument is inferred to be `Any`.
  "-Ywarn-nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Ywarn-nullary-unit", // Warn when nullary methods return Unit.
  "-Ywarn-numeric-widen", // Warn when numerics are widened.
  "-Ywarn-unused:implicits", // Warn if an implicit parameter is unused.
  "-Ywarn-unused:locals", // Warn if a local definition is unused.
  "-Ywarn-unused:params", // Warn if a value parameter is unused.
  "-Ywarn-unused:patvars", // Warn if a variable bound in a pattern is unused.
  "-Ywarn-unused:privates", // Warn if a private member is unused.
  "-Ywarn-value-discard" // Warn when non-Unit expression results are unused.
)

scalacOptions in Test --= Seq(
  "-Ywarn-dead-code"
)

coverageEnabled := true
scalafmtOnCompile := true
parallelExecution in Test := true
coverageExcludedPackages := "<empty>;controllers.javascript;router;models;view.*;config.*;.*(AuthService|BuildInfo|Routes).*"
