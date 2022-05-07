plugins {
  kotlin("jvm")
  id("io.bkbn.sourdough.library.jvm")
  id("io.gitlab.arturbosch.detekt")
  id("com.adarshr.test-logger")
  id("org.jetbrains.dokka")
  id("maven-publish")
  id("java-library")
  id("signing")
}

sourdoughLibrary {
  libraryName.set("Kompendium Locations")
  libraryDescription.set("Supplemental library for Kompendium offering support for Ktor's Location API")
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

testing {
  suites {
    named("test", JvmTestSuite::class) {
      useJUnitJupiter()
    }
  }
}
