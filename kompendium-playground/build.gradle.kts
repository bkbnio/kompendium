plugins {
  kotlin("plugin.serialization") version "1.6.0"
  application
}

dependencies {
  // IMPLEMENTATION
  implementation(projects.kompendiumCore)
  implementation(projects.kompendiumAuth)
  implementation(projects.kompendiumLocations)
  implementation(projects.kompendiumSwaggerUi)

  // Ktor
  val ktorVersion: String by project

  implementation(group = "io.ktor", name = "ktor-server-core", version = ktorVersion)
  implementation(group = "io.ktor", name = "ktor-server-netty", version = ktorVersion)
  implementation(group = "io.ktor", name = "ktor-auth", version = ktorVersion)
  implementation(group = "io.ktor", name = "ktor-auth-jwt", version = ktorVersion)
  implementation(group = "io.ktor", name = "ktor-serialization", version = ktorVersion)
  implementation(group = "io.ktor", name = "ktor-locations", version = ktorVersion)
  implementation(group = "io.ktor", name = "ktor-webjars", version = ktorVersion)

  implementation(group = "org.jetbrains.kotlinx", "kotlinx-serialization-json", version = "1.3.1")

  implementation(group = "joda-time", name = "joda-time", version = "2.10.13")
}
