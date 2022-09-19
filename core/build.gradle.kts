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
  id("org.jetbrains.kotlinx.kover")
}

sourdoughLibrary {
  libraryName.set("Kompendium Core")
  libraryDescription.set("Core functionality for the Kompendium library")
  compilerArgs.set(listOf("-opt-in=kotlin.RequiresOptIn"))
}

dependencies {
  // VERSIONS
  val kotestVersion: String by project
  val ktorVersion: String by project
  val detektVersion: String by project

  // IMPLEMENTATION

  api(projects.kompendiumOas)
  api(projects.kompendiumJsonSchema)

  implementation("io.ktor:ktor-server-core:$ktorVersion")
  implementation("io.ktor:ktor-server-cio:$ktorVersion")
  implementation("io.ktor:ktor-server-html-builder:$ktorVersion")
  implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
  implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
  implementation("ch.qos.logback:logback-classic:1.4.1")

  // Formatting
  detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")

  // TEST FIXTURES

  testFixturesApi("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
  testFixturesApi("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
  testFixturesApi("io.kotest:kotest-property-jvm:$kotestVersion")
  testFixturesApi("io.kotest:kotest-assertions-json-jvm:$kotestVersion")
  testFixturesApi("io.kotest:kotest-assertions-ktor-jvm:4.4.3")

  testFixturesApi("io.ktor:ktor-server-core:$ktorVersion")
  testFixturesApi("io.ktor:ktor-server-test-host:$ktorVersion")
  testFixturesApi("io.ktor:ktor-serialization:$ktorVersion")
  testFixturesApi("io.ktor:ktor-serialization-jackson:$ktorVersion")
  testFixturesApi("io.ktor:ktor-serialization-gson:$ktorVersion")
  testFixturesApi("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
  testFixturesApi("io.ktor:ktor-server-content-negotiation:$ktorVersion")
  testFixturesApi("io.ktor:ktor-server-auth:$ktorVersion")
  testFixturesApi("io.ktor:ktor-server-auth-jwt:$ktorVersion")
  testFixturesApi("io.ktor:ktor-client:$ktorVersion")
  testFixturesApi("io.ktor:ktor-client-cio:$ktorVersion")

  testFixturesApi("dev.forst:ktor-api-key:2.1.1-1")

  testFixturesApi("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
}

testing {
  suites {
    named("test", JvmTestSuite::class) {
      useJUnitJupiter()
    }
  }
}
