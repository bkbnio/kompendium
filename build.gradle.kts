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

tasks.dokkaHtmlMultiModule.configure {
  outputDirectory.set(rootDir.resolve("dokka"))
}

repositories {
  mavenCentral()
}
