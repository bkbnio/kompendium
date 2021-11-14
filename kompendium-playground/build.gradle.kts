plugins {
  id("kotlin-application-conventions")
  kotlin("plugin.serialization") version "1.5.31"
}

dependencies {
  implementation(projects.kompendiumCore)
  implementation(projects.kompendiumAuth)
  implementation(projects.kompendiumSwaggerUi)

  implementation(libs.bundles.ktor)
  implementation(libs.ktor.jackson)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.ktor.serialization)
  implementation(libs.bundles.ktorAuth)
  implementation(libs.bundles.logging)

  implementation("joda-time:joda-time:2.10.13")

  testImplementation("org.jetbrains.kotlin:kotlin-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}
