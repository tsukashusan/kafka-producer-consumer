import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.11.12",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "kafka-producer-consumer",
    resolvers ++= Seq("hortonworks release" at "http://repo.hortonworks.com/content/repositories/",
    "hortonworks public" at "http://repo.hortonworks.com/content/groups/public/"),
    libraryDependencies ++= Seq(scalaTest % Test,
      kafkaClient),
    mainClass in assembly := Some("example.Hello")
  )
javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")
