# Kompendium

[![version](https://img.shields.io/maven-central/v/io.bkbn/kompendium-core?style=flat-square)](https://search.maven.org/search?q=io.bkbn%20kompendium)

## Table of Contents

- [What Is Kompendium](#what-is-kompendium)
- [How to Install](#how-to-install)
- [Library Details](#library-details)
- [Local Development](#local-development)
- [The Playground](#the-playground)

## What is Kompendium

Kompendium is intended to be a minimally invasive OpenApi Specification generator for Ktor. Minimally invasive meaning
that users will use only Ktor native functions when implementing their API, and will supplement with Kompendium code in
order to generate the appropriate spec.

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

# Library Details

Forthcoming, more details on V2 will be published soon :)

## Local Development

Kompendium should run locally right out of the box, no configuration necessary (assuming you have JDK 1.8+ installed).
New features can be built locally and published to your local maven repository with the `./gradlew publishToMavenLocal`
command!

## The Playground

This repo contains a `playground` module that contains a number of working examples showcasing the capabilities of
Kompendium.

Feel free to check it out, or even create your own example!
