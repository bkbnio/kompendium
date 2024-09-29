plugins {
  kotlin("jvm") version "1.9.25" apply false
  kotlin("plugin.serialization") version "1.9.25" apply false
  id("io.bkbn.sourdough.library.jvm") version "0.12.2" apply false
  id("io.bkbn.sourdough.application.jvm") version "0.12.2" apply false
  id("io.bkbn.sourdough.root") version "0.12.2"
  id("org.jetbrains.kotlinx.kover") version "0.8.3"
  id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}

dependencies {
  subprojects.forEach { kover(it) }
}

allprojects {
  group = "io.bkbn"
  version = run {
    val baseVersion =
      project.findProperty("project.version") ?: error("project.version needs to be set in gradle.properties")
    when ((project.findProperty("release") as? String)?.toBoolean()) {
      true -> baseVersion
      else -> "$baseVersion-SNAPSHOT"
    }
  }
}

subprojects {
  plugins.withType(io.bkbn.sourdough.gradle.library.jvm.LibraryJvmPlugin::class) {
    extensions.configure(io.bkbn.sourdough.gradle.library.jvm.LibraryJvmExtension::class) {
      githubOrg.set("bkbnio")
      githubRepo.set("kompendium")
      licenseName.set("MIT License")
      licenseUrl.set("https://mit-license.org")
      developerId.set("unredundant")
      developerName.set("Ryan Brink")
      developerEmail.set("admin@bkbn.io")
    }
  }
}
