plugins {
  id("io.bkbn.sourdough.library")
}

dependencies {
  implementation(projects.kompendiumCore)
  implementation(libs.bundles.ktor)
  implementation(libs.ktor.locations)

  testImplementation(testFixtures(projects.kompendiumCore))
}

testing {
  suites {
    val test by getting(JvmTestSuite::class) {
      useJUnitJupiter()
      dependencies {
        implementation(libs.ktor.serialization)
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.ktor.server.test.host)
      }
    }
  }
}
