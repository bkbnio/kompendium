plugins {
  id("io.bkbn.sourdough.library")
}

dependencies {
  // IMPLEMENTATION

  val ktorVersion: String by project
  implementation(projects.kompendiumCore)
  implementation(group = "io.ktor", name = "ktor-server-core", version = ktorVersion)
  implementation(group = "io.ktor", name = "ktor-locations", version = ktorVersion)

  // TESTING

  testImplementation(testFixtures(projects.kompendiumCore))
}
