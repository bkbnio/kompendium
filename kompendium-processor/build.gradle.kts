plugins {
  kotlin("kapt")
  `java-library`
  `maven-publish`
}

dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation(projects.kompendiumCore)
  implementation("com.google.auto.service:auto-service:1.0")
  kapt("com.google.auto.service:auto-service:1.0")
  testImplementation("org.jetbrains.kotlin:kotlin-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}
//
//publishing {
//  repositories {
//    maven {
//      name = "GithubPackages"
//      url = uri("https://maven.pkg.github.com/lg-backbone/kompendium")
//      credentials {
//        username = System.getenv("GITHUB_ACTOR")
//        password = System.getenv("GITHUB_TOKEN")
//      }
//    }
//  }
//  publications {
//    create<MavenPublication>("kompendium") {
//      from(components["kotlin"])
//    }
//  }
//}
