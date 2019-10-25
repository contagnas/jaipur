name := "jaipur"

version := "0.1"

scalaVersion := "2.13.1"

val monocleVersion = "2.0.0" // depends on cats 2.x

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.0.0",
  "com.beachape" %% "enumeratum" % "1.5.13",
  "com.github.julien-truffaut" %% "monocle-core" % monocleVersion,
  "com.github.julien-truffaut" %% "monocle-macro" % monocleVersion,
  "com.github.julien-truffaut" %% "monocle-law" % monocleVersion % "test",
  "org.typelevel" %% "simulacrum" % "1.0.0",
  "com.typesafe.akka" %% "akka-persistence-typed" % "2.5.26",
  "com.typesafe.akka" %% "akka-http" % "10.1.10",
  "com.typesafe.akka" %% "akka-stream" % "2.5.26",
  "com.typesafe.akka" %% "akka-persistence" % "2.5.26",
)

scalacOptions += "-Ymacro-annotations"
