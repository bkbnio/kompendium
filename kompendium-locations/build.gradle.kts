plugins {
  id("kotlin-library-conventions")
}

dependencies {
  implementation(libs.bundles.ktor)
  implementation(libs.ktor.locations)
  implementation(projects.kompendiumCore)

  testImplementation(libs.ktor.jackson)
  testImplementation(libs.jackson.module.kotlin)
  testImplementation(libs.ktor.server.test.host)
}
