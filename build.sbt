lazy val commonSettings = Seq(
  organization := "org.me",
  version := "0.01",
  scalaVersion := "2.12.8",
)

addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.0")

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  "jitpack.io" at "https://jitpack.io",
)
autoCompilerPlugins := true

val MonocleVersion = "1.5.0"
val ScalaZVersion = "7.2.15"
val ZirconVersion = "2019.1.0-PREVIEW"
val GuiceVersion = "4.2.2"

lazy val nessie = (project in file("."))
    .settings(commonSettings: _*)
    .settings(
      name := "nessie",
      libraryDependencies ++= Seq(
        "com.beachape" %% "enumeratum" % "1.5.13",
        "com.github.julien-truffaut" %% "monocle-core" % MonocleVersion,
        "com.github.julien-truffaut" %% "monocle-macro" % MonocleVersion,
        "com.github.julien-truffaut" %% "monocle-unsafe" % MonocleVersion,
        "com.google.inject" % "guice" % GuiceVersion,
        "com.google.inject.extensions" % "guice-assistedinject" % GuiceVersion,
        "net.codingwell" %% "scala-guice" % "4.2.3",
        "com.typesafe.akka" %% "akka-actor" % "2.5.4",
        "com.typesafe.akka" %% "akka-testkit" % "2.5.4" % "test",
        "com.zenjava" % "javafx-maven-plugin" % "8.8.3",
        "io.reactivex" %% "rxscala" % "0.26.4",
        "com.github.Hexworks.zircon" % "zircon.core" % ZirconVersion,
        "com.github.Hexworks.zircon" % "zircon.core-jvm" % ZirconVersion,
        "com.github.Hexworks.zircon" % "zircon.core-metadata" % ZirconVersion,
        "com.github.Hexworks.zircon" % "zircon.jvm.swing" % ZirconVersion,
        "org.me" %% "scalacommon" % "1.0" changing(),
        "org.mockito" % "mockito-all" % "1.9.5" % "test",
        "org.scala-graph" %% "graph-core" % "1.12.5",
        "org.scala-lang" % "scala-swing" % "2.10.6",
        "org.scalacheck" %% "scalacheck" % "1.13.5" % "test",
        "org.scalafx" %% "scalafx" % "8.0.102-R11",
        "org.scalamock" %% "scalamock-core" % "3.6.0" % "test",
        "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0" % "test",
        "org.scalatest" %% "scalatest" % "3.0.4",
        "org.scalaz" %% "scalaz-concurrent" % ScalaZVersion,
        "org.scalaz" %% "scalaz-core" % ScalaZVersion,
      )
    )

addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full)
