plugins {
  kotlin("jvm")
  kotlin("plugin.serialization")
  id("io.bkbn.sourdough.library.jvm")
  id("io.gitlab.arturbosch.detekt")
  id("com.adarshr.test-logger")
  id("maven-publish")
  id("java-library")
  id("signing")
  id("org.jetbrains.kotlinx.kover")
}

sourdoughLibrary {
  libraryName.set("Kompendium Protobuf java converter")
  libraryDescription.set("Converts Java protobuf generated classes to custom type maps")
  compilerArgs.set(listOf("-opt-in=kotlin.RequiresOptIn"))
}

dependencies {
  // Versions
  val detektVersion: String by project


  implementation(projects.kompendiumJsonSchema)
  implementation("com.google.protobuf:protobuf-java:3.23.0")
  implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.21")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

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

