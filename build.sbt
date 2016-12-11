lazy val root = (project in file(".")).
  settings(
    name := "commtest",
    organization := "com.github.pawelkrol",
    scalacOptions += "-feature",
    scalaVersion := "2.11.8",
    version := "0.01"
  )

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  "org.apache.commons" % "commons-lang3" % "3.5",
  "com.github.pawelkrol" % "cpu-6502-simulator" % "0.02-SNAPSHOT",
  "org.scalatest" %% "scalatest" % "3.0.1"
)

// Disable using the Scala version in output paths and artifacts:
crossPaths := false

publishTo <<= version { v: String =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>https://github.com/pawelkrol/Scala-CommTest</url>
  <licenses>
    <license>
      <name>Scala License</name>
      <url>http://www.scala-lang.org/node/146</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git://github.com/pawelkrol/Scala-CommTest</url>
    <connection>scm:git:git://github.com/pawelkrol/Scala-CommTest.git</connection>
  </scm>
  <developers>
    <developer>
      <id>pawelkrol</id>
      <name>Pawel Krol</name>
      <url>https://github.com/pawelkrol/Scala-CommTest</url>
    </developer>
  </developers>
)
