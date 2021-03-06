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

dockerUpdateLatest := true
dockerUsername := Some("jwiii")
dockerRepository := deriveRegistry()
dockerBaseImage := "adoptopenjdk/openjdk11:debianslim-jre"
dockerExposedPorts := Nil

enablePlugins(JavaServerAppPackaging, DockerPlugin)

def deriveRegistry(): Option[String] =
  if (sys.env.exists {
        case ("MICROK8S", "1") => true
        case ("MICROK8S", "0") => false
        case ("MICROK8S", b)   => b.toBoolean
        case _                 => false
      }) Some("localhost:32000")
  else None
