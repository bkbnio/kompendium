plugins {
  kotlin("jvm")
  kotlin("plugin.serialization")
  id("io.bkbn.sourdough.library.jvm")
  id("io.gitlab.arturbosch.detekt")
  id("com.adarshr.test-logger")
  id("org.jetbrains.dokka")
  id("maven-publish")
  id("java-library")
  id("signing")
  id("java-test-fixtures")
}

sourdoughLibrary {
  libraryName.set("Kompendium Core")
  libraryDescription.set("Core functionality for the Kompendium library")
  compilerArgs.set(listOf("-opt-in=kotlin.RequiresOptIn"))
}

dependencies {
  // VERSIONS
  val ktorVersion = "2.0.1"
  val kotestVersion: String by project

  // IMPLEMENTATION

  api(projects.kompendiumOas)
  api(projects.kompendiumAnnotations)

  implementation("io.ktor:ktor-server-core:$ktorVersion")
  implementation("io.ktor:ktor-server-cio:$ktorVersion")
  implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
  implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
  implementation("ch.qos.logback:logback-classic:1.2.11")

  // TEST FIXTURES

  testFixturesApi(group = "io.kotest", name = "kotest-runner-junit5-jvm", version = kotestVersion)
  testFixturesApi(group = "io.kotest", name = "kotest-assertions-core-jvm", version = kotestVersion)
  testFixturesApi(group = "io.kotest", name = "kotest-property-jvm", version = kotestVersion)
  testFixturesApi(group = "io.kotest", name = "kotest-assertions-json-jvm", version = kotestVersion)
  testFixturesApi(group = "io.kotest", name = "kotest-assertions-ktor-jvm", version = "4.4.3")

//  testFixturesApi(group = "io.ktor", name = "ktor-server-core", version = ktorVersion)
//  testFixturesApi(group = "io.ktor", name = "ktor-server-test-host", version = ktorVersion)
//  testFixturesApi(group = "io.ktor", name = "ktor-jackson", version = ktorVersion)
//  testFixturesApi(group = "io.ktor", name = "ktor-gson", version = ktorVersion)
//  testFixturesApi(group = "io.ktor", name = "ktor-serialization", version = ktorVersion)

  testFixturesApi(group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version = "1.3.2")
}

testing {
  suites {
    named("test", JvmTestSuite::class) {
      useJUnitJupiter()
    }
  }
}
