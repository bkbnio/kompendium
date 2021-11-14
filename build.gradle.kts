import com.adarshr.gradle.testlogger.TestLoggerExtension
import com.adarshr.gradle.testlogger.theme.ThemeType
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.jetbrains.kotlin.jvm") apply false
  id("io.gitlab.arturbosch.detekt") version "1.18.1" apply false
  id("com.adarshr.test-logger") version "3.0.0" apply false
  id("io.github.gradle-nexus.publish-plugin") version "1.1.0" apply true
  id("com.github.jakemarsden.git-hooks") version "0.0.2" apply true
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

  repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven") }
  }

  apply(plugin = "org.jetbrains.kotlin.jvm")
  apply(plugin = "io.gitlab.arturbosch.detekt")
  apply(plugin = "com.adarshr.test-logger")
  apply(plugin = "idea")
  apply(plugin = "jacoco")

  tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
      jvmTarget = "1.8"
    }
  }

  tasks.withType<Test>() {
    finalizedBy(tasks.withType(JacocoReport::class))
  }

  tasks.withType<JacocoReport>() {
    reports {
      html.required.set(true)
      xml.required.set(true)
    }
  }

  configure<JacocoPluginExtension> {
    toolVersion = "0.8.7"
  }

  @Suppress("MagicNumber")
  configure<TestLoggerExtension> {
    theme = ThemeType.MOCHA
    setLogLevel("lifecycle")
    showExceptions = true
    showStackTraces = true
    showFullStackTraces = false
    showCauses = true
    slowThreshold = 2000
    showSummary = true
    showSimpleNames = false
    showPassed = true
    showSkipped = true
    showFailed = true
    showStandardStreams = false
    showPassedStandardStreams = true
    showSkippedStandardStreams = true
    showFailedStandardStreams = true
  }

  configure<DetektExtension> {
    toolVersion = "1.18.1"
    config = files("${rootProject.projectDir}/detekt.yml")
    buildUponDefaultConfig = true
  }

  configure<JavaPluginExtension> {
    withSourcesJar()
    withJavadocJar()
  }
}

subprojects {
  apply(plugin = "java")

  configure<JavaPluginExtension> {
    toolchain {
      languageVersion.set(JavaLanguageVersion.of(8))
    }
  }
}

nexusPublishing {
  repositories {
    sonatype {
      nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
      snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
    }
  }
}
