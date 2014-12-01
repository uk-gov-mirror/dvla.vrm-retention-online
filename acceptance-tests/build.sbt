import Common._

name := "vrm-retention-acceptance-tests"

version := versionString

organization := organisationString

organizationName := organisationNameString

scalaVersion := scalaVersionString

scalacOptions := scalaOptionsSeq

publishTo.<<=(publishResolver)

credentials += sbtCredentials

resolvers ++= projectResolvers

libraryDependencies ++= Seq(
  "info.cukes" %% "cucumber-scala" % "1.1.8" % "test" withSources() withJavadoc(),
  "info.cukes" % "cucumber-java" % "1.1.8" % "test" withSources() withJavadoc(),
  "info.cukes" % "cucumber-picocontainer" % "1.1.8" % "test" withSources() withJavadoc(),
  "info.cukes" % "cucumber-junit" % "1.1.8" % "test" withSources() withJavadoc(),
  "org.apache.httpcomponents" % "httpclient" % "4.3.4" withSources() withJavadoc(),
  "dvla" %% "vehicles-presentation-common" % "2.7-SNAPSHOT" withSources() withJavadoc() exclude("junit", "junit-dep")
)
