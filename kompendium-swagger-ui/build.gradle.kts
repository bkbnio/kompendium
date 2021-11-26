plugins {
  id("kotlin-library-conventions")
}

dependencies {
  api(libs.ktor.webjars)
  implementation(libs.bundles.ktor)
  implementation(libs.webjars.swagger.ui)
  testImplementation(libs.jackson.module.kotlin)
  testImplementation(libs.ktor.server.test.host)
}

tasks.dokkaHtmlPartial.configure {
  dokkaSourceSets {
    configureEach {
      includes.from("Module.md")
    }
  }
}
