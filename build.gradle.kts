plugins {
  id("org.jetbrains.kotlin.jvm") version "1.4.32" apply false
  id("io.gitlab.arturbosch.detekt") version "1.16.0-RC2" apply false
}

allprojects {
  group = "org.leafygreens"
  version = run {
    val baseVersion =
      project.findProperty("project.version") ?: error("project.version must be set in gradle.properties")
    when ((project.findProperty("release") as? String)?.toBoolean()) {
      true -> baseVersion
      else -> "$baseVersion-SNAPSHOT"
    }
  }

  repositories {
    mavenCentral()
  }

  apply(plugin = "org.jetbrains.kotlin.jvm")
  apply(plugin = "io.gitlab.arturbosch.detekt")
  apply(plugin = "idea")

  tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
      jvmTarget = "14"
    }
  }

  configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
    toolVersion = "1.16.0-RC2"
    config = files("${rootProject.projectDir}/detekt.yml")
    buildUponDefaultConfig = true
  }

  configure<JavaPluginExtension> {
    withSourcesJar()
  }
}
