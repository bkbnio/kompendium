import io.bkbn.sourdough.gradle.core.extension.SourdoughLibraryExtension

plugins {
  id("io.bkbn.sourdough.root") version "0.3.0"
  id("com.github.jakemarsden.git-hooks") version "0.0.2"
}

sourdough {
  toolChainJavaVersion.set(JavaLanguageVersion.of(JavaVersion.VERSION_17.majorVersion))
  jvmTarget.set(JavaVersion.VERSION_11.majorVersion)
  compilerArgs.set(listOf("-opt-in=kotlin.RequiresOptIn"))
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
    githubOrg.set("bkbnio")
    githubRepo.set("kompendium")
    libraryName.set("Kompendium")
    libraryDescription.set("A minimally invasive OpenAPI spec generator for Ktor")
    licenseName.set("MIT License")
    licenseUrl.set("https://mit-license.org")
    developerId.set("bkbnio")
    developerName.set("Ryan Brink")
    developerEmail.set("admin@bkbn.io")
  }
}
