# Kompendium

Kompendium is intended to be a non-invasive OpenAPI spec generator for [Ktor](https://ktor.io) APIs. By operating
entirely through Ktor's plugin architecture, Kompendium allows you to incrementally document your API without requiring
you to rip out and replace the amazing code you have already written.

## Compatibility

Earlier versions of Kompendium targeted Ktor 1 and OpenAPI spec 3.0. These docs however, are exclusively focused on
Kompendium 3+, which supports Ktor 2, and outputs OpenAPI 3.1 compliant specs

| Kompendium | Ktor | OpenAPI | 
|------------|------|---------|
| 1.X        | 1    | 3.0     |
| 2.X        | 1    | 3.0     |
| 3.X        | 2    | 3.1     | 

## Getting Started

### Adding the Artifact

All Kompendium artifacts are published to Maven Central. Most Kompendium users will only need to import the core
dependency

```kotlin
dependencies {
  // other dependencies...
  implementation("io.bkbn:kompendium-core:latest.release")
}
```

### Notarizing a Ktor Application

Once we have added the dependencies, installed the `NotarizedApplication` plugin. This is an application-level
Ktor plugin that is used to instantiate and configure Kompendium. Your OpenAPI spec metadata will go here, along with
custom type overrides (typically useful for custom scalars such as dates and times), along with other configurations.

For more detail on the `NotarizedApplication` plugin, please see the [docs](./plugins/notarized_application.md)

### Notarizing a Ktor Route
