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
  libraryName.set("Kompendium Authentication")
  libraryDescription.set("Kompendium library to pair with Ktor Auth to provide authorization info to OpenAPI")
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

testing {
  suites {
    named("test", JvmTestSuite::class) {
      useJUnitJupiter()
    }
  }
}
