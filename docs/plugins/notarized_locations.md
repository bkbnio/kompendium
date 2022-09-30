The Ktor Locations API is an experimental API that allows users to add increased type safety to their defined routes.

You can read more about it [here](https://ktor.io/docs/locations.html).

Kompendium supports Locations through an ancillary module `kompendium-locations`

## Adding the Artifact

Prior to documenting your locations, you will need to add the artifact to your gradle build file.

```kotlin
dependencies {
  implementation("io.bkbn:kompendium-locations:latest.release")
}
```

## Installing Plugin

Once you have installed the dependency, you can install the plugin. The `NotarizedLocations` plugin is an _application_
level plugin, and **must** be install after both the `NotarizedApplication` plugin and the Ktor `Locations` plugin.

```kotlin
private fun Application.mainModule() {
  install(Locations)
  install(NotarizedApplication()) {
    spec = baseSpec
  }
  install(NotarizedLocations()) {
    locations = mapOf(
      Listing::class to NotarizedLocations.LocationMetadata(
        parameters = listOf(
          Parameter(
            name = "name",
            `in` = Parameter.Location.path,
            schema = TypeDefinition.STRING
          ),
          Parameter(
            name = "page",
            `in` = Parameter.Location.path,
            schema = TypeDefinition.INT
          )
        ),
        get = GetInfo.builder {
          summary("Get user by id")
          description("A very neat endpoint!")
          response {
            responseCode(HttpStatusCode.OK)
            responseType<ExampleResponse>()
            description("Will return whether or not the user is real 😱")
          }
        }
      ),
    )
  }
}
```

Here, the `locations` property is a map of `KClass<*>` to metadata describing that locations metadata. This
metadata is functionally identical to how a standard `NotarizedRoute` is defined.

> ⚠️ If you try to map a class that is not annotated with the ktor `@Location` annotation, you will get a runtime
> exception!
