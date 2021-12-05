plugins {
  id("io.bkbn.sourdough.root") version "0.0.1-SNAPSHOT"
  id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
  id("com.github.jakemarsden.git-hooks") version "0.0.2"
}

sourdough {
  sonatypeBaseUrl.set("https://s01.oss.sonatype.org")
  javaVersion.set(JavaVersion.VERSION_11)
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
