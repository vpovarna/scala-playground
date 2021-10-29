name := "scala-playground"

version := "0.1"

scalaVersion := "2.13.6"

lazy val kafkaStreamsVersion = "2.8.0"

lazy val circeVersion = "0.14.1"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-clients" % kafkaStreamsVersion,
  "org.apache.kafka" % "kafka-streams" % kafkaStreamsVersion,
  "org.apache.kafka" %% "kafka-streams-scala" % kafkaStreamsVersion,
  
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion
)
