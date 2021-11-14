plugins {
  id("kotlin-library-conventions")
}

dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation(libs.bundles.ktor)
  implementation(libs.bundles.ktorAuth)
  implementation(projects.kompendiumCore)

  testImplementation(libs.ktor.jackson)
  testImplementation("org.jetbrains.kotlin:kotlin-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
  testImplementation(libs.jackson.module.kotlin)
  testImplementation(libs.ktor.server.test.host)
}
