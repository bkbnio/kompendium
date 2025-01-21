plugins {
  kotlin("jvm")
  kotlin("plugin.serialization")
  id("io.bkbn.sourdough.library.jvm")
  id("io.gitlab.arturbosch.detekt")
  id("com.adarshr.test-logger")
  id("com.vanniktech.maven.publish")
  id("java-library")
  id("signing")
  id("org.jetbrains.kotlinx.kover")
}

sourdoughLibrary {
  libraryName.set("Kompendium Resources")
  libraryDescription.set("Supplemental library for Kompendium offering support for Ktor's Resources API")
}

dependencies {
  // Versions
  val detektVersion: String by project
  val ktorVersion: String by project

  // IMPLEMENTATION

  implementation(projects.kompendiumCore)
  implementation("io.ktor:ktor-server-core:$ktorVersion")
  implementation("io.ktor:ktor-server-resources:$ktorVersion")

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
