import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
  lazy val kafkaClient = "org.apache.kafka" % "kafka-clients" % "0.10.1.2.1.1.0-2"
}
