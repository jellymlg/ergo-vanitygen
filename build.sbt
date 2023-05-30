ThisBuild / version := "1.0.0"

lazy val root = (project in file(".")).settings(name := "ergo-fancy-address")

val sonatypePublic = "Sonatype Public" at "https://oss.sonatype.org/content/groups/public/"
val sonatypeReleases = "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"
val sonatypeSnapshots = "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
resolvers ++= Seq(Resolver.mavenLocal, sonatypeReleases, sonatypeSnapshots, Resolver.mavenCentral)
libraryDependencies ++= Seq(
  "org.scorexfoundation" %% "sigma-state" % "5.0.5",
  "org.ergoplatform" %% "ergo-wallet" % "5.0.7"
)

assembly / assemblyJarName := s"ergo-fancy-address-${version.value}.jar"
