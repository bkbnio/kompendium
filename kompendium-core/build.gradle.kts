plugins {
  id("kotlin-library-conventions")
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
      dependencies {
        implementation("io.kotest:kotest-assertions-ktor-jvm:4.4.3")
        implementation(libs.ktor.serialization)
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.ktor.server.test.host)
      }
    }
  }
}

buildscript {
  dependencies {
    classpath("org.jetbrains.dokka:versioning-plugin:1.6.0")
  }
}

tasks.dokkaHtmlPartial.configure {
  dependencies {
    dokkaPlugin("org.jetbrains.dokka:versioning-plugin:1.6.0")
  }

  dokkaSourceSets {
    configureEach {
      includes.from("Module.md")
    }
  }
}
