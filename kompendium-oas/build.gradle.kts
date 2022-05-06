plugins {
  kotlin("jvm")
  kotlin("plugin.serialization")
  id("io.bkbn.sourdough.library.jvm")
  id("io.gitlab.arturbosch.detekt")
  id("com.adarshr.test-logger")
  id("org.jetbrains.dokka")
  id("maven-publish")
  id("java-library")
  id("signing")
}

sourdough {
  libraryName.set("Kompendium OpenAPI Spec")
  libraryDescription.set("Collections of kotlin data classes modeling the OpenAPI specification")
  compilerArgs.set(listOf("-opt-in=kotlin.RequiresOptIn"))
}

dependencies {
  val kotestVersion: String by project

  implementation(group = "org.jetbrains.kotlinx", "kotlinx-serialization-json", version = "1.3.2")

  testImplementation(group = "io.kotest", name = "kotest-runner-junit5-jvm", version = kotestVersion)
  testImplementation(group = "io.kotest", name = "kotest-assertions-core-jvm", version = kotestVersion)
  testImplementation(group = "io.kotest", name = "kotest-property-jvm", version = kotestVersion)
  testImplementation(group = "io.kotest", name = "kotest-assertions-json-jvm", version = kotestVersion)
  testImplementation(group = "io.kotest", name = "kotest-runner-junit5-jvm", version = kotestVersion)
}

testing {
  suites {
    named("test", JvmTestSuite::class) {
      useJUnitJupiter()
    }
  }
}
