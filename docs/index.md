# Kompendium

Kompendium is intended to be a non-invasive OpenAPI spec generator for [Ktor](https://ktor.io) APIs. By operating
entirely through Ktor's plugin architecture, Kompendium allows you to incrementally document your API without requiring
you to rip out and replace the amazing code you have already written.

## Compatibility

| Kompendium | Ktor | OpenAPI | 
|------------|------|---------|
| 1.X        | 1    | 3.0     |
| 2.X        | 1    | 3.0     |
| 3.X        | 2    | 3.1     | 

> These docs are focused solely on Kompendium 3, previous versions should be considered deprecated and no longer
> maintained

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

```kotlin
private fun Application.mainModule() {
  install(NotarizedApplication()) {
    spec = OpenApiSpec(
      // ...
    )
  }
}
```

At this point, you will have a valid OpenAPI specification generated at runtime, which can be accessed by default
at the `/openapi.json` path of your api.

For more detail on the `NotarizedApplication` plugin, please see the [docs](./plugins/notarized_application.md)

### Notarizing a Ktor Route

Once you have notarized your application, you can begin to notarize individual routes using the `NotarizedRoute` plugin.
This is a route-level Ktor plugin that is used to configure the documentation for a specific endpoint of your API.  The 
route documentation will be piped back to the application-level plugin, and will be automatically injected into the 
OpenApi specification.

For more details on the `NotarizedRoute` plugin, please see the [docs](./plugins/notarized_route.md)
