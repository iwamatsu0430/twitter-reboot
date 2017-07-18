import org.flywaydb.sbt.FlywayPlugin
import org.flywaydb.sbt.FlywayPlugin.autoImport._
import play.sbt.PlayImport.PlayKeys._

lazy val root = (project in file("."))
  .enablePlugins(
    FlywayPlugin,
    PlayScala
  )
  .settings(
    name := "sawtter",
    version := "0.2",
    scalaVersion := "2.11.11",
    playDefaultPort := 9010,

    libraryDependencies ++= Seq(
      ws,
      filters,

      "com.typesafe.play"       %% "play-slick"           % "1.1.1",
      "mysql"                    % "mysql-connector-java" % "5.1.26",
      "org.flywaydb"            %% "flyway-play"          % "3.2.0",
      "io.monix"                %% "shade"                % "1.9.5",
      "org.scalaz"              %% "scalaz-core"          % "7.2.12",

      "org.scalatest"           %% "scalatest"            % "3.0.1"   % "test",
      "org.scalatestplus.play"  %% "scalatestplus-play"   % "2.0.0"   % "test",
      "org.mockito"              % "mockito-core"         % "2.8.9"   % "test"
    )
  )
