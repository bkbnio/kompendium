plugins {
  `java-library`
  `maven-publish`
}

dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation(libs.bundles.ktor)
  testImplementation("org.jetbrains.kotlin:kotlin-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
  testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.0")
  testImplementation("io.ktor:ktor-server-test-host:1.5.3")
}

java {
  withSourcesJar()
}

publishing {
  repositories {
    maven {
      name = "GithubPackages"
      url = uri("https://maven.pkg.github.com/lg-backbone/kompendium")
      credentials {
        username = System.getenv("GITHUB_ACTOR")
        password = System.getenv("GITHUB_TOKEN")
      }
    }
  }
  publications {
    create<MavenPublication>("kompendium") {
      from(components["kotlin"])
      artifact(tasks.sourcesJar)
    }
  }
}
