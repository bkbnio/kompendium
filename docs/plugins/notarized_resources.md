The Ktor Resources API allows users to define their routes in a type-safe manner.

You can read more about it [here](https://ktor.io/docs/type-safe-routing.html).

Kompendium supports Ktor-Resources through an ancillary module `kompendium-resources`

## Adding the Artifact

Prior to documenting your resources, you will need to add the artifact to your gradle build file.

```kotlin
dependencies {
  implementation("io.bkbn:kompendium-resources:$version")
}
```

## Installing Plugin

Once you have installed the dependency, you can install the plugin. The `NotarizedResources` plugin is an _application_ level plugin, and **must** be install after both the `NotarizedApplication` plugin and the Ktor `Resources` plugin.

```kotlin
private fun Application.mainModule() {
  install(Resources)
  install(NotarizedApplication()) {
    spec = baseSpec
  }
  install(NotarizedResources()) {
    resources = mapOf(
      Listing::class to NotarizedResources.ResourceMetadata(
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

Here, the `resources` property is a map of `KClass<*>` to `ResourceMetadata` instance describing that resource. This metadata is functionally identical to how a standard `NotarizedRoute` is defined.

> ⚠️ If you try to map a class that is not annotated with the ktor `@Resource` annotation, you will get a runtime
> exception!

## NotarizedRouteResources

If resources are used within nested routes, the `NotarizedResources()` definition will not know of the preceding route prefixes. In this case, you can use `NotarizedRouteResources()`, similar to the regular `NotarizedRoute()`:

```kotlin
@Serializable
@Resource("/list/{name}/page/{page}")
data class Listing(val name: String, val page: Int)

private fun Application.mainModule() {
  install(Resources)
  install(NotarizedApplication()) {
    spec = baseSpec
  }
  route("/api") {
    routeResourcesDocumentation()
    get<Listing> { listing ->
      call.respondText("Listing ${listing.name}, page ${listing.page}")
    }
  }
}

private fun Route.routeResourcesDocumentation() {
  install(NotarizedRouteResources()) {
    resources = mapOf(
      Listing::class to NotarizedRouteResources.ResourceMetadata(
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

In this case, the generated path will be `/api/list/{name}/page/{page}`, combining the route prefix with the path in the resource.
