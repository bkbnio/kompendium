plugins {
  id("io.bkbn.sourdough.library")
  `java-test-fixtures`
}

dependencies {
  api(projects.kompendiumOas)
  api(projects.kompendiumAnnotations)

  implementation(libs.jackson.module.kotlin)
  implementation(libs.bundles.ktor)

  testFixturesImplementation(libs.bundles.ktor)
  testFixturesImplementation(libs.ktor.server.test.host)
  // todo move to version catalog
  val kotestVersion = "5.0.0"
  testFixturesImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
  testFixturesImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
  testFixturesImplementation("io.kotest:kotest-property-jvm:$kotestVersion")
  testFixturesImplementation("io.kotest:kotest-assertions-json-jvm:$kotestVersion")
  testFixturesImplementation("io.kotest:kotest-assertions-ktor-jvm:4.4.3")
  testFixturesImplementation(libs.ktor.jackson)
}

testing {
  suites {
    val test by getting(JvmTestSuite::class) {
      useJUnitJupiter()

      dependencies {
        val kotestVersion = "5.0.0"
        implementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
        implementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
        implementation("io.kotest:kotest-property-jvm:$kotestVersion")
        implementation("io.kotest:kotest-assertions-json-jvm:$kotestVersion")
        implementation("io.kotest:kotest-assertions-ktor-jvm:4.4.3")
        implementation(libs.ktor.serialization)
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.ktor.server.test.host)
      }
    }
  }
}
