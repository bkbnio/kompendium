plugins {
  id("kotlin-library-conventions")
}

dependencies {
  api(projects.kompendiumOas)
  api(projects.kompendiumAnnotations)
  implementation(libs.jackson.module.kotlin)
  implementation(libs.bundles.ktor)
  testImplementation(libs.ktor.serialization)
  testImplementation(libs.kotlinx.serialization.json)
  testImplementation(libs.ktor.jackson)
  testImplementation(libs.ktor.server.test.host)
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
