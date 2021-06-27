name := "CaraMLTest"

version := "1.0.0"

scalaVersion := "2.12.13"

organization := "io.github.jsarni"
homepage := Some(url("https://github.com/jsarni/CaraMLTest"))
scmInfo := Some(ScmInfo(url("https://github.com/jsarni/CaraMLTest"), "git@github.com:jsarni/CaraMLTest.git"))
developers :=
  List(
    Developer("Juba", "SARNI", "juba.sarni@gmail.com", url("https://github.com/jsarni")),
    Developer("Merzouk", "OUMEDDAH", "merzoukoumeddah@gmail.com ", url("https://github.com/merzouk13")),
    Developer("Aghylas", "SAI", "aghilassai@gmail.com", url("https://github.com/SAI-Aghylas"))
  )

// Dependencies
val spark = "org.apache.spark" %% "spark-mllib" % "3.1.1"
val sparkCore = "org.apache.spark" %% "spark-core" % "3.1.1"
val caraML = "io.github.jsarni" %% "caraml" % "1.0.0"
val appconf = "com.typesafe" % "config" % "1.4.0"

lazy val caraMLTest = (project in file("."))
  .settings(
    name := "CaraML",
    libraryDependencies += spark,
    libraryDependencies += sparkCore,
    libraryDependencies += caraML,
    libraryDependencies += appconf
  )