lazy val commonSettings = Seq(
  organization := "com.github.charmoniumq",
  version := "0.1.0",
  scalaVersion := "2.11.8"
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "Data science experiment",
    libraryDependencies ++= Seq(
      "commons-net"% "commons-net" % "3.5",
      "commons-io" % "commons-io" % "2.5",
      "org.scala-lang.modules" %% "scala-xml" % "1.0.2"
    )
  )
