The Ktor Resources API allows users to define their routes in a type-safe manner.

You can read more about it [here](https://ktor.io/docs/type-safe-routing.html).

Kompendium supports Ktor-Resources through an ancillary module `kompendium-resources`

{% hint style="warning" %}
The resources module contains _two_ plugins: `KompendiumResources` and `KompendiumResource`. You will find more
information on both below, but in a nutshell, the former is an application level plugin intended to define your entire
application, while the latter is a route level approach should you wish to split out your route definitions.
{% endhint %}

## Adding the Artifact

Prior to documenting your resources, you will need to add the artifact to your gradle build file.

```kotlin
dependencies {
  implementation("io.bkbn:kompendium-resources:$version")
}
```

## NotarizedResources

The `NotarizedResources` plugin is an _application_ level plugin, and **must** be installed after both the
`NotarizedApplication` plugin and the Ktor `Resources` plugin. It is intended to be used to document your entire
application in a single block.

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

Here, the `resources` property is a map of `KClass<*>` to `ResourceMetadata` instance describing that resource. This
metadata is functionally identical to how a standard `NotarizedRoute` is defined.

{% hint style="danger" %}
If you try to map a class that is not annotated with the ktor `@Resource` annotation, you will get a runtime exception!
{% endhint %}

## NotarizedResource

If you prefer a route-based approach similar to `NotarizedRoute`, you can use the `NotarizedResource<MyResourceType>()`
plugin instead of `NotarizedResources`. It will combine paths from any parent route with the route defined in the
resource, exactly as Ktor itself does:

```kotlin
@Serializable
@Resource("/list/{name}/page/{page}")
data class Listing(val name: String, val page: Int)

private fun Application.mainModule() {
  install(Resources)
  route("/api") {
    listingDocumentation()
    get<Listing> { listing ->
      call.respondText("Listing ${listing.name}, page ${listing.page}")
    }
  }
}

private fun Route.listingDocumentation() {
  install(NotarizedResource<Listing>()) {
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
    )
    get = GetInfo.builder {
      summary("Get user by id")
      description("A very neat endpoint!")
      response {
        responseCode(HttpStatusCode.OK)
        responseType<ExampleResponse>()
        description("Will return whether or not the user is real 😱")
      }
    }
  }
}
```

In this case, the generated path will be `/api/list/{name}/page/{page}`, combining the route prefix with the path in the
resource.

{% hint style="danger" %}
If you try to map a class that is not annotated with the ktor `@Resource` annotation, you will get a runtime exception!
{% endhint %}
