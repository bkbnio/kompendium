plugins {
  id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
  id("com.github.jakemarsden.git-hooks") version "0.0.2"
  id("org.jetbrains.dokka")
}

gitHooks {
  setHooks(
    mapOf(
      "pre-commit" to "detekt",
      "pre-push" to "test"
    )
  )
}

nexusPublishing {
  repositories {
    sonatype {
      nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
      snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
    }
  }
}

version = run {
  val baseVersion =
    project.findProperty("project.version") ?: error("project.version needs to be set in gradle.properties")
  when ((project.findProperty("release") as? String)?.toBoolean()) {
    true -> baseVersion
    else -> "$baseVersion-SNAPSHOT"
  }
}

tasks.dokkaHtmlMultiModule.configure {
  val version = project.version.toString()
  outputDirectory.set(rootDir.resolve("dokka/$version"))
}

repositories {
  mavenCentral()
}
