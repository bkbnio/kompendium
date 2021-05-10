plugins {
  id("org.jetbrains.kotlin.jvm") version "1.4.32" apply false
  id("io.gitlab.arturbosch.detekt") version "1.16.0-RC2" apply false
  id("com.adarshr.test-logger") version "3.0.0" apply false
  id("io.github.gradle-nexus.publish-plugin") version "1.1.0" apply true
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

  tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
      jvmTarget = "11"
    }
  }

  configure<com.adarshr.gradle.testlogger.TestLoggerExtension> {
    setTheme("standard")
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

  configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
    toolVersion = "1.16.0-RC2"
    config = files("${rootProject.projectDir}/detekt.yml")
    buildUponDefaultConfig = true
  }

  configure<JavaPluginExtension> {
    withSourcesJar()
  }
}

nexusPublishing {
  repositories {
    sonatype {
      username.set(System.getenv("SONATYPE_USER"))
      password.set(System.getenv("SONATYPE_PASSWORD"))
      nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"))
      snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
    }
  }
}
