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

//tasks.dokkaHtmlPartial.configure {
//  dokkaSourceSets {
//    configureEach {
//      includes.from("Module.md")
//    }
//  }
//}
