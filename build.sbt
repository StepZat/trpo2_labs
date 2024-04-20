ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.13"

// Добавление репозитория, содержащего ScalaFX
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

// Зависимость ScalaFX
libraryDependencies += "org.scalafx" %% "scalafx" % "16.0.0-R24"

lazy val root = (project in file("."))
  .settings(
    name := "lab1",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := "2.13.13",
    libraryDependencies += "org.scalafx" %% "scalafx" % "16.0.0-R24"
  )
