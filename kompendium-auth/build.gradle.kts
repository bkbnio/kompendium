plugins {
  id("kotlin-library-conventions")
}

dependencies {
  implementation(libs.bundles.ktor)
  implementation(libs.bundles.ktorAuth)
  implementation(projects.kompendiumCore)
}

testing {
  suites {
    val test by getting(JvmTestSuite::class) {
      dependencies {
        implementation(libs.ktor.jackson)
        implementation(libs.jackson.module.kotlin)
        implementation("io.kotest:kotest-assertions-ktor-jvm:4.4.3")
        implementation("io.ktor:ktor-server-test-host:1.6.5")
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
