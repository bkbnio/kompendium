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
  libraryName.set("Kompendium Locations")
  libraryDescription.set("Supplemental library for Kompendium offering support for Ktor's Location API")
  licenseName.set("MIT License")
  licenseUrl.set("https://mit-license.org")
  developerId.set("unredundant")
  developerName.set("Ryan Brink")
  developerEmail.set("admin@bkbn.io")
  compilerArgs.set(listOf("-opt-in=kotlin.RequiresOptIn"))
}

dependencies {
  // IMPLEMENTATION

  val ktorVersion: String by project
  implementation(projects.kompendiumCore)
  implementation(group = "io.ktor", name = "ktor-server-core", version = ktorVersion)
  implementation(group = "io.ktor", name = "ktor-locations", version = ktorVersion)

  // TESTING

  testImplementation(testFixtures(projects.kompendiumCore))
}
