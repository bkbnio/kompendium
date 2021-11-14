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
  implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.19.0-RC1")
  implementation("com.adarshr:gradle-test-logger-plugin:3.1.0")
}
