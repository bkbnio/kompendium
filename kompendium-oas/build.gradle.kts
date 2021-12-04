plugins {
  id("io.bkbn.sourdough.library")
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
      }
    }
  }
}
