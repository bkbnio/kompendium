plugins {
  application
}

dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  implementation(projects.kompendiumCore)
  implementation(projects.kompendiumAuth)
  implementation(projects.kompendiumSwaggerUi)

  implementation(libs.bundles.ktor)
  implementation(libs.bundles.ktorAuth)
  implementation(libs.bundles.logging)

  testImplementation("org.jetbrains.kotlin:kotlin-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

application {
  @Suppress("DEPRECATION")
  mainClassName = "io.bkbn.kompendium.playground.MainKt"
  applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true") // TODO I don't think this is working 😢
}
