name := """akka-http-websocket-microservices"""


version := "1.0"

val commonDependencies =  {
  val AkkaHttpVersion   = "2.4.8"
  Seq(
    "com.typesafe.akka" %% "akka-http-experimental" % AkkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-testkit-experimental" % "2.4.2-RC3",
    "org.scalatest"     %% "scalatest"                 % "2.2.6",
    "org.mockito"       % "mockito-core"                    % "1.9.5"

  )
}

lazy val projectSettings = Seq(
  scalaVersion := "2.11.8",
  fork in Test := true
)

def baseProject(name: String): Project = (
  Project(name, file(name))
    settings (projectSettings: _*)
  )

lazy val root = (
  project.in(file("."))
    aggregate(api, order, account)
  )

lazy val api = (
  baseProject("api")
    settings(libraryDependencies ++= commonDependencies)
  )

lazy val order = (
  baseProject("order")
    settings(libraryDependencies ++= commonDependencies)
  )

lazy val account = (
  baseProject("account")
    settings(libraryDependencies ++= commonDependencies)
  )