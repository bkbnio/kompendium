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
  val kotestVersion: String by project

  // IMPLEMENTATION

  api(projects.kompendiumOas)
  api(projects.kompendiumJsonSchema)

  implementation("io.ktor:ktor-server-core:2.1.0")
  implementation("io.ktor:ktor-server-cio:2.1.0")
  implementation("io.ktor:ktor-server-html-builder:2.1.0")
  implementation("io.ktor:ktor-server-content-negotiation:2.1.0")
  implementation("io.ktor:ktor-serialization-kotlinx-json:2.1.0")
  implementation("ch.qos.logback:logback-classic:1.2.11")

  // TEST FIXTURES

  testFixturesApi("io.kotest:kotest-runner-junit5-jvm:5.4.1")
  testFixturesApi("io.kotest:kotest-assertions-core-jvm:5.4.1")
  testFixturesApi("io.kotest:kotest-property-jvm:5.4.1")
  testFixturesApi("io.kotest:kotest-assertions-json-jvm:5.4.1")
  testFixturesApi("io.kotest:kotest-assertions-ktor-jvm:4.4.3")

  testFixturesApi("io.ktor:ktor-server-core:2.1.0")
  testFixturesApi("io.ktor:ktor-server-test-host:2.1.0")
  testFixturesApi("io.ktor:ktor-serialization:2.1.0")
  testFixturesApi("io.ktor:ktor-serialization-jackson:2.1.0")
  testFixturesApi("io.ktor:ktor-serialization-gson:2.1.0")
  testFixturesApi("io.ktor:ktor-serialization-kotlinx-json:2.1.0")
  testFixturesApi("io.ktor:ktor-server-content-negotiation:2.1.0")

  testFixturesApi("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
}

testing {
  suites {
    named("test", JvmTestSuite::class) {
      useJUnitJupiter()
    }
  }
}
