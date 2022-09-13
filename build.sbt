ThisBuild / scalaVersion     := "2.13.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

val animusVersion               = "0.1.15"
val flywayVersion               = "9.3.0"
val laminarVersion              = "0.14.2"
val postgresVersion             = "42.5.0"
val slf4jVersion                = "2.0.0"
val zioHttpVersion              = "2.0.0-RC9"
val zioJsonVersion              = "0.3.0-RC8"
val zioLoggingVersion           = "2.1.0"
val zioQuillVersion             = "4.4.1"
val zioTestContainersVersion    = "0.8.0"
val zioVersion                  = "2.0.2"
val zioMetricsConnectorsVersion = "2.0.0-RC6"

Global / onChangedBuildSource := ReloadOnSourceChanges

val sharedSettings = Seq(
  libraryDependencies ++= Seq(
    "dev.zio" %%% "zio-json" % zioJsonVersion
  ),
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding",
    "utf8",
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-unchecked",
    "-Xfatal-warnings",
    "-Ymacro-annotations"
  )
)

lazy val root = (project in file("."))
  .aggregate(backend)
  .settings(name := "backend-technical-test")

lazy val backend = (project in file("backend"))
  .settings(
    name := "backend-technical-test",
    libraryDependencies ++= Seq(
      "dev.zio"               %% "zio"                               % zioVersion,
      "dev.zio"               %% "zio-macros"                        % zioVersion,
      "dev.zio"               %% "zio-metrics-connectors"            % zioMetricsConnectorsVersion,
      "dev.zio"               %% "zio-test"                          % zioVersion % Test,
      "dev.zio"               %% "zio-test-sbt"                      % zioVersion % Test,
      "io.d11"                %% "zhttp"                             % zioHttpVersion,
      "io.getquill"           %% "quill-jdbc-zio"                    % zioQuillVersion,
      "org.postgresql"         % "postgresql"                        % postgresVersion,
      "org.flywaydb"           % "flyway-core"                       % flywayVersion,
      "io.github.scottweaver" %% "zio-2-0-testcontainers-postgresql" % zioTestContainersVersion,
      "io.github.scottweaver" %% "zio-2-0-db-migration-aspect"       % zioTestContainersVersion,
      "dev.zio"               %% "zio-logging-slf4j"                 % zioLoggingVersion,
      "org.slf4j"              % "slf4j-api"                         % slf4jVersion,
      "org.slf4j"              % "slf4j-simple"                      % slf4jVersion
    ),
    Test / fork := true,
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
  .enablePlugins(JavaAppPackaging)
  .settings(sharedSettings)
  .enablePlugins(FlywayPlugin)
  .settings(
    flywayUrl      := "jdbc:postgresql://localhost:5432/postgres",
    flywayUser     := "postgres",
    flywayPassword := ""
  )
