ThisBuild / version := "1.0"

lazy val root = (project in file(".")).settings(name := "ergo-vanitygen")

lazy val jarName = settingKey[String]("Custom JAR name")
jarName := {s"${name.value}-${version.value}.jar"}

val sonatypePublic = "Sonatype Public" at "https://oss.sonatype.org/content/groups/public/"
val sonatypeReleases = "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"
val sonatypeSnapshots = "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
resolvers ++= Seq(Resolver.mavenLocal, sonatypeReleases, sonatypeSnapshots, Resolver.mavenCentral)
libraryDependencies ++= Seq(
  "org.scorexfoundation" %% "sigma-state" % "5.0.5",
  "org.ergoplatform" %% "ergo-wallet" % "5.0.7",
  "com.github.scopt" %% "scopt" % "4.1.0"
)

Compile / compile / scalacOptions ++= Seq("-release", "8")
assembly / assemblyJarName := jarName.value

enablePlugins(SbtProguard)
Proguard / proguard / javaOptions := Seq("-Xmx2G")
Proguard / proguardVersion := "7.3.2"
Proguard / proguardOptions ++= Seq(
  "-dontnote", "-dontwarn", "-ignorewarnings",
  ProguardOptions.keepMain("fanta.vanitygen.Main"),
  "-dontobfuscate", "-keepattributes Signature", "-dontoptimize",
  "-keep class org.bouncycastle.jcajce.** {*;}",
  "-keep class special.collection.Coll {*;}",
  "-keep class sigmastate.** {*;}"
)
Proguard / proguardInputs := Seq((assembly / assemblyOutputPath).value)
Proguard / proguardOutputs := Seq(new java.io.File(s"${(assembly / assemblyOutputPath).value.getParent}/proguard/${jarName.value}"))
Proguard / proguardInputFilter := (_ => None)
Proguard / proguardMerge := false
Proguard / proguard := (Proguard / proguard).dependsOn(assembly).value
