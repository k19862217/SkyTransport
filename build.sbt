import com.github.play2war.plugin._

name := """SkyTransport"""

version := "1.0-SNAPSHOT"

Play2WarPlugin.play2WarSettings

Play2WarKeys.servletVersion := "3.1"

fork := true

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

unmanagedBase := baseDirectory.value / "lib"

unmanagedJars in Compile <<= baseDirectory map { base => (base / "lib" ** "*.jar").classpath }

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  specs2 % Test,
  "mysql" % "mysql-connector-java" % "5.1.27",
  "com.typesafe.slick" %% "slick" % "3.1.0",
  "org.json4s" % "json4s-jackson_2.11" % "3.3.0",
  "com.github.tototoshi" %% "slick-joda-mapper" % "2.1.0",
  "net.tanesha.recaptcha4j" % "recaptcha4j" % "0.0.7"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
