plugins {
  kotlin("jvm")
  kotlin("plugin.serialization") version "1.6.10"
  id("io.bkbn.sourdough.library.jvm") version "0.5.5"
  id("io.gitlab.arturbosch.detekt") version "1.19.0"
  id("com.adarshr.test-logger") version "3.1.0"
  id("org.jetbrains.dokka")
  id("maven-publish")
  id("java-library")
  id("signing")
}

sourdough {
  githubOrg.set("bkbnio")
  githubRepo.set("kompendium")
  libraryName.set("Kompendium OpenAPI Spec")
  libraryDescription.set("Collections of kotlin data classes modeling the OpenAPI specification")
  licenseName.set("MIT License")
  licenseUrl.set("https://mit-license.org")
  developerId.set("unredundant")
  developerName.set("Ryan Brink")
  developerEmail.set("admin@bkbn.io")
  compilerArgs.set(listOf("-opt-in=kotlin.RequiresOptIn"))
}

dependencies {
  implementation(group = "org.jetbrains.kotlinx", "kotlinx-serialization-json", version = "1.3.1")
}

testing {
  suites {
    named("test", JvmTestSuite::class) {
      useJUnitJupiter()
    }
  }
}
