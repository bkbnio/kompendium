plugins {
  application
}

dependencies {
  implementation(projects.kompendiumCore)
  implementation(projects.kompendiumAuth)
  implementation(projects.kompendiumLocations)
  implementation(projects.kompendiumSwaggerUi)

  implementation(libs.bundles.ktor)
  implementation(libs.ktor.jackson)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.ktor.serialization)
  implementation(libs.bundles.ktorAuth)
  implementation(libs.ktor.locations)
  implementation(libs.bundles.logging)

  implementation("joda-time:joda-time:2.10.13")
}
