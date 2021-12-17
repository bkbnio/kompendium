import io.bkbn.sourdough.gradle.core.extension.SourdoughLibraryExtension

plugins {
  id("io.bkbn.sourdough.root") version "0.1.1"
  id("com.github.jakemarsden.git-hooks") version "0.0.2"
}

sourdough {
  toolChainJavaVersion = JavaVersion.VERSION_17
  jvmTarget = JavaVersion.VERSION_11.majorVersion
  compilerArgs = listOf("-opt-in=kotlin.RequiresOptIn")
}

gitHooks {
  setHooks(
    mapOf(
      "pre-commit" to "detekt",
      "pre-push" to "test"
    )
  )
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
  apply(plugin = "io.bkbn.sourdough.library")

  configure<SourdoughLibraryExtension> {
    githubOrg = "bkbnio"
    githubRepo = "kompendium"
    githubUsername = System.getenv("GITHUB_ACTOR")
    githubToken = System.getenv("GITHUB_TOKEN")
    libraryName = "Kompendium" // TODO Set on a per-module basis?
    libraryDescription = "A minimally invasive OpenAPI spec generator for Ktor"
    licenseName = "MIT License"
    licenseUrl = "https://mit-license.org/"
    developerId = "bkbnio"
    developerName = "Ryan Brink"
    developerEmail = "admin@bkbn.io"
  }
}
