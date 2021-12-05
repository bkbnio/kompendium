plugins {
  id("io.bkbn.sourdough.library")
  `java-test-fixtures`
}

dependencies {
  api(projects.kompendiumOas)
  api(projects.kompendiumAnnotations)

  implementation(libs.jackson.module.kotlin)
  implementation(libs.bundles.ktor)

  testFixturesApi(libs.bundles.ktor)
  testFixturesApi(libs.ktor.server.test.host)
  val kotestVersion = "5.0.0"
  testFixturesApi("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
  testFixturesApi("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
  testFixturesApi("io.kotest:kotest-property-jvm:$kotestVersion")
  testFixturesApi("io.kotest:kotest-assertions-json-jvm:$kotestVersion")
  testFixturesApi("io.kotest:kotest-assertions-ktor-jvm:4.4.3")
  testFixturesApi("io.ktor:ktor-server-test-host:1.6.6")
  testFixturesApi(libs.ktor.jackson)
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
