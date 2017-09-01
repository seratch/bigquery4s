lazy val root = (project in file("."))
  .settings(
    organization := "com.github.seratch",
    name := "bigquery4s",
    version := "0.6",
    scalaVersion := "2.12.3",
    crossScalaVersions := Seq("2.12.3", "2.11.11", "2.10.6"),
    libraryDependencies ++= Seq(
      "com.google.apis"         %  "google-api-services-bigquery" % "v2-rev355-1.22.0",
      "com.google.oauth-client" %  "google-oauth-client"          % "1.22.0",
      "com.google.oauth-client" %  "google-oauth-client-jetty"    % "1.22.0",
      "com.google.http-client"  %  "google-http-client-jackson2"  % "1.22.0",
      "ch.qos.logback"          %  "logback-classic"              % "1.2.3"   % Test,
      "org.scalatest"           %% "scalatest"                    % "3.0.4"   % Test
    ),
    parallelExecution in Test := false,
    logBuffered in Test := false,
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),
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
  ).settings(scalariformSettings)
