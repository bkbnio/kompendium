plugins {
  `kotlin-dsl`
}

repositories {
  // Use the plugin portal to apply community plugins in convention plugins.
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin")
}
