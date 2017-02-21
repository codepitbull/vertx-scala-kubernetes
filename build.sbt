import sbt.Package._
import sbt._

version := "0.1-SNAPSHOT"
name := "vertx-scala-kubernetes-demo"
organization := "io.vertx"

enablePlugins(DockerPlugin)

scalaVersion := "2.12.1"

libraryDependencies ++= Vector (
  Library.vertxLangScala,
  Library.vertxCodegen,
  Library.vertxWeb,
  Library.vertxHazelcast,
  Library.hazelcastKubernetes,
  Library.scalaTest       % "test"
)

packageOptions += ManifestAttributes(
  ("Main-Verticle", "scala:io.vertx.scala.sbt.StarterVerticle"))

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case PathList("META-INF", "services", "com.hazelcast.spi.discovery.DiscoveryStrategyFactory") => MergeStrategy.first
  case PathList("META-INF", xs @ _*) => MergeStrategy.last
  case PathList("codegen.json") => MergeStrategy.discard
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

dockerfile in docker := {
  // The assembly task generates a fat JAR file
  val artifact: File = assembly.value
  val artifactTargetPath = s"/app/${artifact.name}"
  val confTargetPath = s"/app/conf/cluster.xml"

  new Dockerfile {
    from("frolvlad/alpine-oraclejdk8:slim")
    add(artifact, artifactTargetPath)
    entryPoint("java", "-jar", artifactTargetPath, "-cluster")
    expose(8666, 5701)
  }
}

imageNames in docker := Seq(
  // Sets a name with a tag that contains the project version
  ImageName(
    namespace = Some(organization.value),
    repository = name.value,
    tag = Some("v" + version.value)
  )
)