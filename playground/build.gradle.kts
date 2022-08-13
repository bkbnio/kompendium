plugins {
  kotlin("jvm")
  kotlin("plugin.serialization")
  id("io.bkbn.sourdough.application.jvm")
  id("application")
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

  implementation("io.ktor:ktor-server-core:2.1.0")
  implementation("io.ktor:ktor-server-netty:2.1.0")
  implementation("io.ktor:ktor-server-auth:2.1.0")
  implementation("io.ktor:ktor-server-auth-jwt:2.1.0")
  implementation("io.ktor:ktor-serialization:2.1.0")
  implementation("io.ktor:ktor-server-content-negotiation:2.1.0")
  implementation("io.ktor:ktor-serialization-kotlinx-json:2.1.0")
  implementation("io.ktor:ktor-serialization-jackson:2.1.0")
  implementation("io.ktor:ktor-serialization-gson:2.1.0")
  implementation("io.ktor:ktor-server-locations:2.1.0")

  // Logging
  implementation("org.apache.logging.log4j:log4j-api-kotlin:1.2.0")
  implementation("org.apache.logging.log4j:log4j-api:2.17.2")
  implementation("org.apache.logging.log4j:log4j-core:2.18.0")
  implementation("org.slf4j:slf4j-api:1.7.36")
  implementation("org.slf4j:slf4j-simple:1.7.36")


  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
  implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

  implementation("joda-time:joda-time:2.10.14")
}
repositories {
  mavenCentral()
}
