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
  libraryName.set("Kompendium JSON Schema")
  libraryDescription.set("Json Schema Generator")
  compilerArgs.set(listOf("-opt-in=kotlin.RequiresOptIn"))
}

dependencies {
  // Versions
  val detektVersion: String by project
  val kotlinVersion: String by project
  val kotlinSerializeVersion: String by project

  // Kompendium
  api(projects.kompendiumEnrichment)

  implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializeVersion")

  // Formatting
  detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")

  testImplementation(testFixtures(projects.kompendiumCore))
}

testing {
  suites {
    named("test", JvmTestSuite::class) {
      useJUnitJupiter()
    }
  }
}
