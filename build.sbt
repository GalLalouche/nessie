lazy val commonSettings = Seq(
  organization := "org.me",
  version := "0.01",
  scalaVersion := "2.12.8")

resolvers += Resolver.sonatypeRepo("releases")
val MonocleVersion = "1.5.0"
autoCompilerPlugins := true
lazy val nessie = (project in file("."))
    .settings(commonSettings: _*)
    .settings(
      name := "nessie",
      libraryDependencies ++= Seq(
        "com.beachape" %% "enumeratum" % "1.5.13",
        "com.github.julien-truffaut" %% "monocle-core" % MonocleVersion,
        "com.github.julien-truffaut" %% "monocle-macro" % MonocleVersion,
        "com.github.julien-truffaut" %% "monocle-unsafe" % MonocleVersion,
        "com.typesafe.akka" %% "akka-actor" % "2.5.4",
        "com.typesafe.akka" %% "akka-testkit" % "2.5.4" % "test",
        "com.zenjava" % "javafx-maven-plugin" % "8.8.3",
        "io.reactivex" %% "rxscala" % "0.26.4",
        "org.me" %% "scalacommon" % "1.0" changing(),
        "org.mockito" % "mockito-all" % "1.9.5" % "test",
        "org.scala-graph" %% "graph-core" % "1.11.5",
        "org.scala-lang" % "scala-swing" % "2.10.6",
        "org.scalacheck" %% "scalacheck" % "1.13.5" % "test",
        "org.scalafx" %% "scalafx" % "8.0.102-R11",
        "org.scalamock" %% "scalamock-core" % "3.6.0" % "test",
        "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0" % "test",
        "org.scalatest" %% "scalatest" % "3.0.4",
        "org.scalaz" %% "scalaz-core" % "7.2.15"
      )
    )

addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full)
