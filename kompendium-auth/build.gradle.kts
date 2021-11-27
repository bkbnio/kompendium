plugins {
  id("kotlin-library-conventions")
}

dependencies {
  implementation(libs.bundles.ktor)
  implementation(libs.bundles.ktorAuth)
  implementation(projects.kompendiumCore)

  testImplementation(libs.ktor.jackson)
  testImplementation(libs.jackson.module.kotlin)
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
