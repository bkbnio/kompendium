plugins {
  kotlin("jvm")
  kotlin("plugin.serialization")
  id("io.bkbn.sourdough.application.jvm")
  id("application")
}

sourdough {
  compilerArgs.set(listOf("-opt-in=kotlin.RequiresOptIn"))
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
  implementation(group = "io.ktor", name = "ktor-jackson", version = ktorVersion)
  implementation(group = "io.ktor", name = "ktor-gson", version = ktorVersion)
  implementation(group = "io.ktor", name = "ktor-locations", version = ktorVersion)

  // Logging
  implementation("org.apache.logging.log4j:log4j-api-kotlin:1.2.0")
  implementation("org.apache.logging.log4j:log4j-api:2.18.0")
  implementation("org.apache.logging.log4j:log4j-core:2.18.0")
  implementation("org.slf4j:slf4j-api:1.7.36")
  implementation("org.slf4j:slf4j-simple:1.7.36")


  implementation(group = "org.jetbrains.kotlinx", "kotlinx-serialization-json", version = "1.3.2")
  implementation(group = "org.jetbrains.kotlinx", "kotlinx-datetime", version = "0.3.2")

  implementation(group = "joda-time", name = "joda-time", version = "2.11.0")
}
repositories {
  mavenCentral()
}
