plugins {
  id("io.bkbn.sourdough.library")
}


dependencies {
  // IMPLEMENTATION

  val ktorVersion: String by project
  implementation(projects.kompendiumCore)
  implementation(group = "io.ktor", name = "ktor-server-core", version = ktorVersion)
  implementation(group = "io.ktor", name = "ktor-auth", version = ktorVersion)
  implementation(group = "io.ktor", name = "ktor-auth-jwt", version = ktorVersion)

  // TESTING

  testImplementation(testFixtures(projects.kompendiumCore))
}
