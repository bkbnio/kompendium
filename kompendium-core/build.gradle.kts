plugins {
  id("kotlin-library-conventions")
}

dependencies {
  api(projects.kompendiumOas)
  implementation(libs.jackson.module.kotlin)
  implementation(libs.bundles.ktor)
  testImplementation(libs.ktor.serialization)
  testImplementation(libs.kotlinx.serialization.json)
  testImplementation(libs.ktor.jackson)
  testImplementation(libs.ktor.server.test.host)
}
