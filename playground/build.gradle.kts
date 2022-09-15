plugins {
  kotlin("jvm")
  kotlin("plugin.serialization")
  id("io.bkbn.sourdough.application.jvm")
  id("application")
  id("org.jetbrains.kotlinx.kover")
}

sourdoughApp {
  compilerArgs.set(listOf("-opt-in=kotlin.RequiresOptIn"))
}

dependencies {
  // IMPLEMENTATION
  implementation(projects.kompendiumCore)
  implementation(projects.kompendiumLocations)

  // Ktor
  val ktorVersion: String by project

  implementation("io.ktor:ktor-server-core:$ktorVersion")
  implementation("io.ktor:ktor-server-cio:$ktorVersion")
  implementation("io.ktor:ktor-server-auth:$ktorVersion")
  implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")
  implementation("io.ktor:ktor-serialization:$ktorVersion")
  implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
  implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
  implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
  implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
  implementation("io.ktor:ktor-serialization-gson:$ktorVersion")
  implementation("io.ktor:ktor-server-locations:$ktorVersion")

  // Logging
  implementation("org.apache.logging.log4j:log4j-api-kotlin:1.2.0")
  implementation("org.apache.logging.log4j:log4j-api:2.18.0")
  implementation("org.apache.logging.log4j:log4j-core:2.18.0")
  implementation("org.slf4j:slf4j-api:2.0.1")
  implementation("org.slf4j:slf4j-simple:2.0.1")


  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
  implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

  implementation("joda-time:joda-time:2.11.1")
}
