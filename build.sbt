name := "training"

version := "0.1"

scalaVersion := "2.12.11"

sbtVersion := "1.2.8"

libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed" % "2.5.32"

libraryDependencies ++= Seq("com.typesafe.akka" %% "akka-stream" % "2.5.32",
"com.typesafe.akka" %% "akka-http" % "10.2.4")

libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.2.4"