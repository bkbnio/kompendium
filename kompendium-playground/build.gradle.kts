plugins {
  kotlin("plugin.serialization") version "1.5.0"
  application
}

dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

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

  testImplementation("org.jetbrains.kotlin:kotlin-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

application {
  @Suppress("DEPRECATION")
  mainClassName = "io.bkbn.kompendium.playground.MainKt"
  applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true") // TODO I don't think this is working 😢
}
