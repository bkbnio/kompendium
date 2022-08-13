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

  implementation(projects.kompendiumCore)
  implementation("io.ktor:ktor-server-core:2.1.0")
  implementation("io.ktor:ktor-server-auth:2.1.0")
  implementation("io.ktor:ktor-server-auth-jwt:2.1.0")

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
