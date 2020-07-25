name := "example-k8s-scala-operator"
version := "0.1"
scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  "io.skuber" %% "skuber" % "2.4.0",
  "org.slf4j" % "slf4j-simple" % "1.7.26",
  "com.typesafe.akka" %% "akka-stream" % "2.6.6",
  "com.typesafe.akka" %% "akka-slf4j" % "2.6.6",
  "com.typesafe.akka" %% "akka-http" % "10.1.12"
)
