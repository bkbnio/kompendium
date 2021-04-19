plugins {
  application
}

dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  implementation(projects.kompendiumCore)
  implementation(projects.kompendiumAuth)

  implementation(libs.bundles.ktor)
  implementation(libs.bundles.logging)
  implementation("io.ktor:ktor-auth:1.5.3")
  implementation("io.ktor:ktor-auth-jwt:1.5.3")

  testImplementation("org.jetbrains.kotlin:kotlin-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

application {
  @Suppress("DEPRECATION")
  mainClassName = "org.leafygreens.kompendium.playground.MainKt"
  applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true") // TODO I don't think this is working 😢
}
