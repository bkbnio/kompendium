# Kompendium

Welcome to Kompendium, the straight-forward, minimally-invasive OpenAPI generator for Ktor.  

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

## Setting up the Kompendium Plugin

Kompendium is instantiated as a Ktor Feature/Plugin.  It can be added to your API as follows

```kotlin
private fun Application.mainModule() {
    // Installs the Kompendium Plugin and sets up baseline server metadata
    install(Kompendium) {
        spec = OpenApiSpec(/*..*/)
    }
    // ...
}
```

## Notarization

The concept of notarizing routes / exceptions / etc. is central to Kompendium.  More details on _how_ to notarize your 
API can be found in the kompendium-core module.
