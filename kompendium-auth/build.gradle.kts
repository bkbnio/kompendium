plugins {
  id("io.bkbn.sourdough.library")
}


dependencies {
  implementation(projects.kompendiumCore)
  implementation(libs.bundles.ktor)
  implementation(libs.bundles.ktorAuth)

  testImplementation(testFixtures(projects.kompendiumCore))
}
