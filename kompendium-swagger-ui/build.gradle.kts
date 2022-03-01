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

sourdough {
  libraryName.set("Kompendium Swagger")
  libraryDescription.set("Offers Swagger as a bundled WebJAR for Ktor")
}

dependencies {
  val ktorVersion: String by project
  implementation(group = "io.ktor", name = "ktor-server-core", version = ktorVersion)
  implementation(group = "org.webjars", name = "swagger-ui", version = "4.5.2")
}

testing {
  suites {
    named("test", JvmTestSuite::class) {
      useJUnitJupiter()
    }
  }
}
