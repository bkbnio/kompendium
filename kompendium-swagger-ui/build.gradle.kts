plugins {
  kotlin("jvm")
  id("io.bkbn.sourdough.library.jvm")
  id("io.gitlab.arturbosch.detekt")
  id("com.adarshr.test-logger")
  id("org.jetbrains.dokka")
  id("maven-publish")
  id("java-library")
  id("signing")
  id("java-test-fixtures")
}

sourdough {
  libraryName.set("Kompendium Swagger")
  libraryDescription.set("Offers Swagger as a bundled WebJAR for Ktor")
}

dependencies {
  val ktorVersion: String by project

  implementation(projects.kompendiumCore)
  implementation(group = "io.ktor", name = "ktor-server-core", version = ktorVersion)
  implementation(group = "org.webjars", name = "webjars-locator-core", version = "0.51")
  implementation(group = "org.webjars", name = "swagger-ui", version = "4.11.1")

  testImplementation(testFixtures(projects.kompendiumCore))
}

testing {
  suites {
    named("test", JvmTestSuite::class) {
      useJUnitJupiter()
    }
  }
}
