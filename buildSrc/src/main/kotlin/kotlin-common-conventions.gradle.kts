import com.adarshr.gradle.testlogger.theme.ThemeType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm")
  id("io.gitlab.arturbosch.detekt")
  id("com.adarshr.test-logger")
  idea
  jacoco
}

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

testing {
  suites {
    val test by getting(JvmTestSuite::class) {
      useJUnitJupiter()

      dependencies {
        val kotestVersion = "5.0.0"
        implementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
        implementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
        implementation("io.kotest:kotest-property-jvm:$kotestVersion")
        implementation("io.kotest:kotest-assertions-json-jvm:$kotestVersion")
      }
    }
  }
}

jacoco {
  toolVersion = "0.8.7"
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

tasks.withType<KotlinCompile>().configureEach {
  sourceCompatibility = "11"
  kotlinOptions {
    jvmTarget = "11"
    freeCompilerArgs = freeCompilerArgs + listOf("-opt-in=kotlin.RequiresOptIn")
  }
}

java {
  withSourcesJar()
  withJavadocJar()
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(11))
    vendor.set(JvmVendorSpec.ADOPTOPENJDK)
  }
}

detekt {
  toolVersion = "1.19.0-RC2"
  config = files("${rootProject.projectDir}/detekt.yml")
  buildUponDefaultConfig = true
}

testlogger {
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
