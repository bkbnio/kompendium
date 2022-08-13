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
  implementation(projects.kompendiumAuth)
  implementation(projects.kompendiumLocations)

  // Ktor
  val ktorVersion: String by project

  implementation("io.ktor:ktor-server-core:2.0.1")
  implementation("io.ktor:ktor-server-netty:2.0.1")
  implementation("io.ktor:ktor-server-auth:2.0.1")
  implementation("io.ktor:ktor-server-auth-jwt:2.0.1")
  implementation("io.ktor:ktor-serialization:2.0.1")
  implementation("io.ktor:ktor-serialization-jackson:2.0.1")
  implementation("io.ktor:ktor-serialization-gson:2.0.1")
  implementation("io.ktor:ktor-server-locations:2.0.1")

  // Logging
  implementation("org.apache.logging.log4j:log4j-api-kotlin:1.1.0")
  implementation("org.apache.logging.log4j:log4j-api:2.17.2")
  implementation("org.apache.logging.log4j:log4j-core:2.17.2")
  implementation("org.slf4j:slf4j-api:1.7.36")
  implementation("org.slf4j:slf4j-simple:1.7.36")


  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
  implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")

  implementation("joda-time:joda-time:2.10.14")
}
repositories {
  mavenCentral()
}