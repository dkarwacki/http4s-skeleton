lazy val catsVersion = "2.3.1"
lazy val http4sVersion = "0.21.22"
lazy val circeVersion = "0.12.3"
lazy val scalaTestVersion = "3.2.2"
lazy val mockitoVersion = "3.2.2.0"
lazy val pureConfigVersion = "0.15.0"
lazy val newTypeVersion = "0.4.4"

lazy val catsDependencies = Seq(
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.typelevel" %% "cats-effect" % catsVersion,
  "org.typelevel" %% "cats-effect-laws" % catsVersion % Test
)

lazy val circeDependencies = Seq(
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion
)

lazy val http4sDependencies = Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion
)

lazy val scalaTestDependencies = Seq(
  "org.scalactic" %% "scalactic" % scalaTestVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
)

lazy val mockitoDependencies = Seq(
  "org.scalatestplus" %% "mockito-3-4" % mockitoVersion % "test"
)

lazy val pureConfigDependencies = Seq(
  "com.github.pureconfig" %% "pureconfig" % pureConfigVersion
)

lazy val newTypeDependencies = Seq(
  "io.estatico" %% "newtype" % newTypeVersion
)

lazy val root = (project in file("."))
  .settings(
    name := "http4s-skeleton",
    version := "0.1",
    organization := "com.dkarwacki",
    scalaVersion := "2.13.6",
    scalacOptions += "-Ymacro-annotations",
    libraryDependencies ++= circeDependencies
      ++ catsDependencies
      ++ http4sDependencies
      ++ scalaTestDependencies
      ++ mockitoDependencies
      ++ pureConfigDependencies
      ++ newTypeDependencies
  )
