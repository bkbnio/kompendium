plugins {
  kotlin("jvm")
  id("io.bkbn.sourdough.library.jvm") version "0.5.4"
  id("io.gitlab.arturbosch.detekt") version "1.19.0"
  id("com.adarshr.test-logger") version "3.1.0"
  id("org.jetbrains.dokka")
  id("maven-publish")
  id("java-library")
}

sourdough {
  githubOrg.set("bkbnio")
  githubRepo.set("kompendium")
  libraryName.set("Kompendium Authentication")
  libraryDescription.set("Kompendium library to pair with Ktor Auth to provide authorization info to OpenAPI")
  licenseName.set("MIT License")
  licenseUrl.set("https://mit-license.org")
  developerId.set("unredundant")
  developerName.set("Ryan Brink")
  developerEmail.set("admin@bkbn.io")
}

dependencies {
  // IMPLEMENTATION

  val ktorVersion: String by project
  implementation(projects.kompendiumCore)
  implementation(group = "io.ktor", name = "ktor-server-core", version = ktorVersion)
  implementation(group = "io.ktor", name = "ktor-auth", version = ktorVersion)
  implementation(group = "io.ktor", name = "ktor-auth-jwt", version = ktorVersion)

  // TESTING

  testImplementation(testFixtures(projects.kompendiumCore))
}
