plugins {
  kotlin("jvm")
  id("io.bkbn.sourdough.library.jvm") version "0.5.5"
  id("io.gitlab.arturbosch.detekt") version "1.19.0"
  id("com.adarshr.test-logger") version "3.1.0"
  id("org.jetbrains.dokka")
  id("maven-publish")
  id("java-library")
  id("signing")
  id("java-test-fixtures")
}

sourdough {
  githubOrg.set("bkbnio")
  githubRepo.set("kompendium")
  libraryName.set("Kompendium Core")
  libraryDescription.set("Core functionality for the Kompendium library")
  licenseName.set("MIT License")
  licenseUrl.set("https://mit-license.org")
  developerId.set("unredundant")
  developerName.set("Ryan Brink")
  developerEmail.set("admin@bkbn.io")
  compilerArgs.set(listOf("-opt-in=kotlin.RequiresOptIn"))
}

dependencies {
  // VERSIONS
  val ktorVersion: String by project
  val kotestVersion: String by project

  // IMPLEMENTATION

  api(projects.kompendiumOas)
  api(projects.kompendiumAnnotations)

  implementation(group = "io.ktor", name = "ktor-server-core", version = ktorVersion)
  implementation(group = "io.ktor", name = "ktor-html-builder", version = ktorVersion)

  // TEST FIXTURES

  testFixturesApi(group = "io.kotest", name = "kotest-runner-junit5-jvm", version = kotestVersion)
  testFixturesApi(group = "io.kotest", name = "kotest-assertions-core-jvm", version = kotestVersion)
  testFixturesApi(group = "io.kotest", name = "kotest-property-jvm", version = kotestVersion)
  testFixturesApi(group = "io.kotest", name = "kotest-assertions-json-jvm", version = kotestVersion)
  testFixturesApi(group = "io.kotest", name = "kotest-assertions-ktor-jvm", version = "4.4.3")

  testFixturesApi(group = "io.ktor", name = "ktor-server-core", version = ktorVersion)
  testFixturesApi(group = "io.ktor", name = "ktor-server-test-host", version = ktorVersion)
  testFixturesApi(group = "io.ktor", name = "ktor-jackson", version = ktorVersion)
  testFixturesApi(group = "io.ktor", name = "ktor-serialization", version = ktorVersion)

  testFixturesApi(group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version = "1.3.2")
}

testing {
  suites {
    named("test", JvmTestSuite::class) {
      useJUnitJupiter()
    }
  }
}
