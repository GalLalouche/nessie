lazy val commonSettings = Seq(
  organization := "org.me",
  version := "0.01",
  scalaVersion := "2.11.8")

resolvers += Resolver.sonatypeRepo("releases")
autoCompilerPlugins := true
lazy val nessie = (project in file("."))
    .settings(commonSettings: _*)
    .settings(
      name := "nessie",
      libraryDependencies ++= Seq(
        "com.github.julien-truffaut" %% "monocle-core" % "1.3.2",
        "com.github.julien-truffaut" %% "monocle-macro" % "1.3.2",
        "com.github.julien-truffaut" %% "monocle-unsafe" % "1.3.2",
        "com.typesafe.akka" %% "akka-actor" % "2.4.8",
        "com.typesafe.akka" %% "akka-testkit" % "2.4.8" % "test",
        "io.reactivex" %% "rxscala" % "0.26.4",
        "org.me" %% "scalacommon" % "1.0" changing(),
        "org.mockito" % "mockito-all" % "1.9.5" % "test",
        "org.scala-lang" % "scala-swing" % "2.10.6",
        "org.scalacheck" %% "scalacheck" % "1.12.1" % "test",
        "org.scalafx" %% "scalafx" % "8.0.92-R10",
        "org.scalamock" %% "scalamock-core" % "3.2.2" % "test",
        "org.scalamock" %% "scalamock-scalatest-support" % "3.2.2" % "test",
        "org.scalatest" %% "scalatest" % "2.2.6" % "test",
        "org.scalaz" %% "scalaz-core" % "7.2.4"
      ))

addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full)
