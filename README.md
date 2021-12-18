# Kompendium

[![version](https://img.shields.io/maven-central/v/io.bkbn/kompendium-core?style=flat-square)](https://search.maven.org/search?q=io.bkbn%20kompendium)

## ⚠️ Attention ⚠️

Kompendium V2 is approaching 🚀 The V2 alpha is now live, and should be considered the de-facto implementation going
forward. It includes a near complete rewrite of the Kompendium codebase. As such, there will likely be bugs. If you
would like to remain on V1, you can find the code and docs on the `v1` branch.

Additionally, all V1 code can still be downloaded as packages from Maven Central.

More information on the extensive changes between V1 and V2 can be found in the [`CHANGELOG.md`](./CHANGELOG.md)

## What is Kompendium

## How to install

Kompendium publishes all releases to Maven Central. As such, using the release versions of `Kompendium` is as simple as
declaring it as an implementation dependency in your `build.gradle.kts`

```kotlin
repositories {
  mavenCentral()
}

dependencies {
  implementation("io.bkbn:kompendium-core:latest.release")
  implementation("io.bkbn:kompendium-auth:latest.release")
  implementation("io.bkbn:kompendium-swagger-ui:latest.release")

  // Other dependencies...
}
```

The last two dependencies are optional.

If you want to get a little spicy 🤠 every merge of Kompendium is published to the GitHub package registry. Pulling from
GitHub is slightly more involved, but such is the price you pay for bleeding edge fake data generation.

```kotlin
// 1 Setup a helper function to import any Github Repository Package
// This step is optional but I have a bunch of stuff stored on github so I find it useful 😄
fun RepositoryHandler.github(packageUrl: String) = maven {
    name = "GithubPackages"
    url = uri(packageUrl)
    credentials {
      username = java.lang.System.getenv("GITHUB_USER")
      password = java.lang.System.getenv("GITHUB_TOKEN")
    }
  }

// 2 Add the repo in question (in this case Kompendium)
repositories {
  github("https://maven.pkg.github.com/bkbnio/kompendium")
}

// 3 Add the package like any normal dependency
dependencies {
  implementation("io.bkbn:kompendium-core:latest.release")
}

```

## Local Development

Kompendium should run locally right out of the box, no configuration necessary (assuming you have JDK 1.8+ installed).
New features can be built locally and published to your local maven repository with the `./gradlew publishToMavenLocal`
command!

