plugins {
  id("kotlin-library-conventions")
}

dependencies {
  implementation(libs.bundles.ktor)
  implementation(libs.ktor.locations)
  implementation(projects.kompendiumCore)
}

testing {
  suites {
    val test by getting(JvmTestSuite::class) {
      dependencies {
        implementation("io.kotest:kotest-assertions-ktor-jvm:4.4.3")
        implementation(libs.ktor.serialization)
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.ktor.jackson)
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