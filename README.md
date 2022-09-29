# Kompendium

[![version](https://img.shields.io/maven-central/v/io.bkbn/kompendium-core?style=flat-square)](https://search.maven.org/search?q=io.bkbn%20kompendium)

Documentation is stored in the `docs` folder and is hosted [here](https://bkbn.gitbook.io/kompendium)

### Compatability

| Kompendium | Ktor | OpenAPI | 
|------------|------|---------|
| 1.X        | 1    | 3.0     |
| 2.X        | 1    | 3.0     |
| 3.X        | 2    | 3.1     | 

## How to install

Kompendium publishes all releases to Maven Central. As such, using the release versions of `Kompendium` is as simple as
declaring it as an implementation dependency in your `build.gradle.kts`

```kotlin
repositories {
  mavenCentral()
}

dependencies {
  implementation("io.bkbn:kompendium-core:latest.release")
}
```

In addition to publishing releases to Maven Central, a snapshot version gets published to GitHub Packages on every merge
to `main`. These can be consumed by adding the repository to your gradle build file. Instructions can be
found [here](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry#using-a-published-package)

## Local Development

Kompendium should run locally right out of the box, no configuration necessary (assuming you have JDK 11+ installed).
New features can be built locally and published to your local maven repository with the `./gradlew publishToMavenLocal`
command!

## The Playground

This repo contains a `playground` module that contains a number of working examples showcasing the capabilities of
Kompendium.

Feel free to check it out, or even create your own example!
