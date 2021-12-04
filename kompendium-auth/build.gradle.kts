plugins {
  id("io.bkbn.sourdough.library")
}


dependencies {
  implementation(projects.kompendiumCore)
  implementation(libs.bundles.ktor)
  implementation(libs.bundles.ktorAuth)

  testImplementation(testFixtures(projects.kompendiumCore))
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
        implementation("io.ktor:ktor-server-test-host:1.6.6")
      }
    }
  }
}
