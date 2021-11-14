plugins {
  id("kotlin-library-conventions")
}

dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  api(projects.kompendiumOas)
  implementation(libs.jackson.module.kotlin)
  implementation(libs.bundles.ktor)
  testImplementation("org.jetbrains.kotlin:kotlin-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
  testImplementation(libs.ktor.serialization)
  testImplementation(libs.kotlinx.serialization.json)
  testImplementation(libs.ktor.jackson)
  testImplementation(libs.ktor.server.test.host)
}
