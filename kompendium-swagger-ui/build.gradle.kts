plugins {
  kotlin("jvm")
  id("io.bkbn.sourdough.library.jvm") version "0.5.4"
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
  libraryName.set("Kompendium Swagger")
  libraryDescription.set("Offers Swagger as a bundled WebJAR for Ktor")
  licenseName.set("MIT License")
  licenseUrl.set("https://mit-license.org")
  developerId.set("unredundant")
  developerName.set("Ryan Brink")
  developerEmail.set("admin@bkbn.io")
}

dependencies {
  val ktorVersion: String by project
  implementation(group = "io.ktor", name = "ktor-server-core", version = ktorVersion)
  implementation(group = "io.ktor", name = "ktor-webjars", version = ktorVersion)
  implementation(group = "org.webjars", name = "swagger-ui", version = "4.1.3-1")
}

testing {
  suites {
    named("test", JvmTestSuite::class) {
      useJUnitJupiter()
    }
  }
}
