plugins {
  id("kotlin-common-conventions")
  java
  `java-library`
  `maven-publish`
  signing
}

java {
  withSourcesJar()
  withJavadocJar()
}

publishing {
  repositories {
    maven {
      name = "GithubPackages"
      url = uri("https://maven.pkg.github.com/bkbnio/kompendium")
      credentials {
        username = System.getenv("GITHUB_ACTOR")
        password = System.getenv("GITHUB_TOKEN")
      }
    }
  }
  publications {
    create<MavenPublication>(project.name) {
      from(components["kotlin"])
      artifact(tasks.findByName("sourcesJar"))
      artifact(tasks.findByName("javadocJar"))
      groupId = project.group.toString()
      artifactId = project.name.toLowerCase()
      version = project.version.toString()

      pom {
        name.set("Kompendium")
        description.set("A minimally invasive OpenAPI spec generator for Ktor")
        url.set("https://github.com/bkbnio/Kompendium")
        licenses {
          license {
            name.set("MIT License")
            url.set("https://mit-license.org/")
          }
        }
        developers {
          developer {
            id.set("bkbnio")
            name.set("Ryan Brink")
            email.set("admin@bkbn.io")
          }
        }
        scm {
          connection.set("scm:git:git://github.com/bkbnio/Kompendium.git")
          developerConnection.set("scm:git:ssh://github.com/bkbnio/Kompendium.git")
          url.set("https://github.com/bkbnio/Kompendium.git")
        }
      }
    }
  }
}

if ((project.findProperty("release") as? String)?.toBoolean() == true) {
  signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications)
  }
}
