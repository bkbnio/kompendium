plugins {
  kotlin("jvm")
  id("io.bkbn.sourdough.library.jvm")
  id("io.gitlab.arturbosch.detekt")
  id("com.adarshr.test-logger")
  id("maven-publish")
  id("java-library")
  id("signing")
  id("org.jetbrains.kotlinx.kover")
}

sourdoughLibrary {
  libraryName.set("Kompendium Locations")
  libraryDescription.set("Supplemental library for Kompendium offering support for Ktor's Location API")
  compilerArgs.set(listOf("-opt-in=kotlin.RequiresOptIn"))
}

dependencies {
  // Versions
  val detektVersion: String by project

  // IMPLEMENTATION

  implementation(projects.kompendiumCore)
  implementation("io.ktor:ktor-server-core:3.0.0")
  implementation("io.ktor:ktor-server-locations:2.3.12")

  // TESTING

  testImplementation(testFixtures(projects.kompendiumCore))

  // Formatting
  detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")
}

testing {
  suites {
    named("test", JvmTestSuite::class) {
      useJUnitJupiter()
    }
  }
}
