lazy val root = (project in file("."))
  .settings(
    organization := "com.github.seratch",
    name := "bigquery4s",
    version := "0.9-SNAPSHOT",
    scalaVersion := "2.12.7",
    crossScalaVersions := Seq("2.12.7", "2.11.12", "2.10.7"),
    libraryDependencies ++= Seq(
      "com.google.apis"         %  "google-api-services-bigquery" % "v2-rev405-1.25.0",
      "com.google.oauth-client" %  "google-oauth-client"          % "1.25.0",
      "com.google.oauth-client" %  "google-oauth-client-jetty"    % "1.25.0",
      "com.google.http-client"  %  "google-http-client-jackson2"  % "1.25.0",
      "ch.qos.logback"          %  "logback-classic"              % "1.2.3"   % Test,
      "org.scalatest"           %% "scalatest"                    % "3.0.5"   % Test
    ),
    parallelExecution in Test := false,
    logBuffered in Test := false,
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),
    publishTo := _publishTo(version.value),
    publishMavenStyle := true,
    pomIncludeRepository := { x => false },
    pomExtra := <url>https://github.com/seratch/bigquery4s/</url>
  <licenses>
    <license>
      <name>MIT License</name>
      <url>http://www.opensource.org/licenses/mit-license.php</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:seratch/bigquery4s.git</url>
    <connection>scm:git:git@github.com:seratch/bigquery4s.git</connection>
  </scm>
  <developers>
    <developer>
      <id>seratch</id>
      <name>Kazuhiro Sera</name>
      <url>http://git.io/sera</url>
    </developer>
  </developers>
  )

def _publishTo(v: String) = {
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
