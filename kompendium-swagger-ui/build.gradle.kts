plugins {
  id("kotlin-library-conventions")
}

dependencies {
  api(libs.ktor.webjars)
  implementation(libs.bundles.ktor)
  implementation(libs.webjars.swagger.ui)
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
