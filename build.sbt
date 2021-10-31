name := "scala-playground"

version := "0.1"

scalaVersion := "2.13.6"

lazy val kafkaStreamsVersion = "2.8.0"
lazy val circeVersion = "0.14.1"
lazy val akkaVersion = "2.6.5"
lazy val akkaHttpVersion = "10.2.0"
lazy val akkaHttpJsonSerializersVersion = "1.34.0"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-clients" % kafkaStreamsVersion,
  "org.apache.kafka" % "kafka-streams" % kafkaStreamsVersion,
  "org.apache.kafka" %% "kafka-streams-scala" % kafkaStreamsVersion,
  
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,

  // akka-streams
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion, 

  //akka-http
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" %akkaHttpVersion,
  "de.heikoseeberger" %% "akka-http-circe" % akkaHttpJsonSerializersVersion,
  "de.heikoseeberger" %% "akka-http-jackson" % akkaHttpJsonSerializersVersion

)
