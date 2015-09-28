import com.github.play2war.plugin._

Play2WarKeys.servletVersion := "3.0"

Play2WarPlugin.play2WarSettings

sources in (Compile,doc) := Seq.empty

publishArtifact in (Compile, packageDoc) := false

name := "Tickets"

version := "1.0-SNAPSHOT"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

lazy val root = (project in file(".")).enablePlugins(PlayJava).settings(updateOptions := updateOptions.value.withCachedResolution(true))

scalaVersion := "2.11.2"

resolvers ++= Seq(
    Resolver.url("Edulify Repository", url("https://edulify.github.io/modules/releases/"))(Resolver.ivyStylePatterns),
    "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
    Resolver.url("github repo for html5tags", url("http://loicdescotte.github.io/releases/"))(Resolver.ivyStylePatterns)
)

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  javaWs,
  cache,
  filters,
  "mysql" % "mysql-connector-java" % "5.1.34",
  "com.lambdaworks" % "scrypt" % "1.4.0",
  "com.typesafe.play" %% "play-mailer" % "2.4.0-RC1",
  "org.pegdown" % "pegdown" % "1.4.2",
  "org.ocpsoft.prettytime" % "prettytime" % "3.2.7.Final",
// "org.tuckey" % "urlrewritefilter" % "4.0.4",
  "com.edulify" %% "sitemapper" % "1.1.7"
)

// PlayKeys.playOmnidoc := false

incOptions := incOptions.value.withNameHashing(true)

TwirlKeys.templateImports ++= Seq("util._")