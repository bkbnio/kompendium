plugins {
  id("io.bkbn.sourdough.library")
  kotlin("plugin.serialization") version "1.6.0"
}

dependencies {
  implementation(group = "org.jetbrains.kotlinx", "kotlinx-serialization-json", version = "1.3.1")
}
