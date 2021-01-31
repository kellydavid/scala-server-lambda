lazy val Scala212Version = "2.12.13"
lazy val Scala213Version = "2.13.4"

def scalacVersionOptions(scalaVersion: String) =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, 12)) => Seq("-Ypartial-unification")
    case _ => Nil
  }

lazy val commonSettings = Seq(
  scalaVersion := Scala212Version,
  crossScalaVersions := Seq(Scala212Version, Scala213Version),
)

lazy val root = project
  .in(file("."))
  .settings(commonSettings)
  .settings(noPublishSettings)
  .aggregate(common, tests, http4s, http4sZio, akka, exampleHttp4s, exampleAkka)

inThisBuild(List(
  organization := "com.dvdkly",
  homepage := Some(url("https://github.com/kellydavid/scala-server-lambda")),
  licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/kellydavid/scala-server-lambda"),
      "scm:git@github.com:kellydavid/scala-server-lambda.git"
    )),
  developers := List(
    Developer(
      id = "howardjohn",
      name = "John Howard",
      email = "johnbhoward96@gmail.com",
      url = url("https://github.com/howardjohn/")
    ),
    Developer(
      id = "kellydavid",
      name = "David Kelly",
      email = "kellydavid178@gmail.com",
      url = url("https://dvdkly.com")
    )
  )
))

lazy val CirceVersion = "0.12.1"
lazy val ScalaTestVersion = "3.1.0"
lazy val Http4sVersion = "0.21.15"

lazy val common = project
  .in(file("common"))
  .settings(commonSettings)
  .settings(
    moduleName := "scala-server-lambda-common",
    libraryDependencies ++=
      Seq(
        "io.circe" %% "circe-generic" % CirceVersion,
        "io.circe" %% "circe-parser" % CirceVersion,
        "org.scalatest" %% "scalatest" % ScalaTestVersion % "test"
      )
  )

lazy val tests = project
  .in(file("tests"))
  .settings(commonSettings)
  .settings(noPublishSettings)
  .settings(
    moduleName := "scala-server-lambda-tests",
    libraryDependencies ++=
      Seq(
        "org.scalatest" %% "scalatest" % ScalaTestVersion
      )
  )
  .dependsOn(common)

lazy val http4s = project
  .in(file("http4s-lambda"))
  .settings(commonSettings)
  .settings(
    name := "scala-server-lambda-http4s",
    moduleName := "scala-server-lambda-http4s",
    scalacOptions ++= scalacVersionOptions(scalaVersion.value),
    libraryDependencies ++= {
      Seq(
        "org.http4s" %% "http4s-core" % Http4sVersion,
        "org.scalatest" %% "scalatest" % ScalaTestVersion % "test",
        "org.http4s" %% "http4s-dsl" % Http4sVersion % "test",
        "org.http4s" %% "http4s-circe" % Http4sVersion % "test"
      )
    }
  )
  .dependsOn(common)
  .dependsOn(tests % "test")

lazy val http4sZio = project
  .in(file("http4s-lambda-zio"))
  .settings(commonSettings)
  .settings(
    name := "scala-server-lambda-http4s-zio",
    moduleName := "scala-server-lambda-http4s-zio",
    scalacOptions ++= scalacVersionOptions(scalaVersion.value),
    libraryDependencies ++= {
      Seq(
        "org.http4s" %% "http4s-core" % Http4sVersion,
        "org.scalatest" %% "scalatest" % ScalaTestVersion % "test",
        "org.http4s" %% "http4s-dsl" % Http4sVersion % "test",
        "org.http4s" %% "http4s-circe" % Http4sVersion % "test",
        "dev.zio" %% "zio" % "1.0.1",
        "dev.zio" %% "zio-interop-cats" % "2.1.4.0"
      )
    }
  )
  .dependsOn(common)
  .dependsOn(tests % "test")
  .dependsOn(http4s % "test->test;compile->compile")

lazy val akka = project
  .in(file("akka-http-lambda"))
  .settings(commonSettings)
  .settings(
    name := "scala-server-lambda-akka-http",
    moduleName := "scala-server-lambda-akka-http",
    scalacOptions ++= scalacVersionOptions(scalaVersion.value),
    libraryDependencies ++= {
      Seq(
        "com.typesafe.akka" %% "akka-http" % "10.1.10",
        "com.typesafe.akka" %% "akka-stream" % "2.5.26",
        "org.scalatest" %% "scalatest" % ScalaTestVersion % "test"
      )
    }
  )
  .dependsOn(common)
  .dependsOn(tests % "test")

lazy val exampleHttp4s = project
  .in(file("example-http4s"))
  .settings(noPublishSettings)
  .settings(commonSettings)
  .settings(
    moduleName := "example-http4s",
    assemblyJarName in assembly := "example-http4s.jar",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-dsl" % Http4sVersion
    )
  )
  .dependsOn(http4s)

lazy val exampleAkka = project
  .in(file("example-akka-http"))
  .settings(noPublishSettings)
  .settings(commonSettings)
  .settings(
    moduleName := "example-akka-http",
    assemblyJarName in assembly := "example-akka-http.jar",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % "10.1.10",
      "com.typesafe.akka" %% "akka-stream" % "2.5.26"
    )
  )
  .dependsOn(akka)

lazy val noPublishSettings = Seq(
  skip in publish := true
)

