The `NotarizedApplication` plugin sits at the center of the entire Kompendium setup. It is a pre-requisite to
installing any other Kompendium plugins.

# Configuration

Very little configuration is needed for a basic documentation setup, but
several configuration options are available that allow you to modify Kompendium to fit your needs.

## Spec

This is where you will define the server metadata that lives outside the scope of any specific route. For
full information, you can inspect the `OpenApiSpec` data class, and of course
reference [OpenAPI spec](https://spec.openapis.org/oas/v3.1.0) itself.

> ⚠️ Please note, the `path` field of the `OpenApiSpec` is intended to be filled in by `NotarizedRoute` plugin
> definitions. Writing custom paths manually could lead to unexpected behavior

## Custom Routing

For public facing APIs, having the default endpoint exposed at `/openapi.json` is totally fine. However, if you need
more granular control over the route that exposes the generated schema, you can modify the `openApiJson` config value.

For example, if we want to hide our schema behind a basic auth check, we could do the following

```kotlin
private fun Application.mainModule() {
  // Install content negotiation, auth, etc...
  install(NotarizedApplication()) {
    // ...
    openApiJson = {
      authenticate("basic") {
        route("/openapi.json") {
          get {
            call.respond(
              HttpStatusCode.OK,
              this@route.application.attributes[KompendiumAttributes.openApiSpec]
            )
          }
        }
      }
    }
  }
}
```

## Custom Types

Kompendium is _really_ good at converting simple scalar and complex objects into JsonSchema compliant specs. However,
there is a subset of values that cause it trouble. These are most commonly classes that produce "complex scalars",
such as dates and times, along with object representations of scalars such as `BigInteger`.

In situations like this, you will need to define a map of custom types to JsonSchema definitions that Kompendium can use
to short-circuit its type analysis.

For example, say we would like to serialize `kotlinx.datetime.Instant` entities as a field in our response objects. We
would need to add it as a custom type.

```kotlin
private fun Application.mainModule() {
  // ...
  install(NotarizedApplication()) {
    spec = baseSpec
    customTypes = mapOf(
      typeOf<Instant>() to TypeDefinition(type = "string", format = "date-time")
    )
  }
}
```

Doing this will save it in a cache that our `NotarizedRoute` plugin definitions will check from prior to attempting to
perform type inspection.

This means that we only need to define our custom type once, and then Kompendium will reuse it across the entire
application.

> While intended for custom scalars, there is nothing stopping you from leveraging custom types to circumvent type
> analysis
> on any class you choose. If you have an alternative method of generating JsonSchema definitions, you could put them
> all
> in this map and effectively prevent Kompendium from having to do any reflection

## Schema Configurator

The `SchemaConfigurator` is an interface that allows users to bridge the gap between Kompendium serialization and custom
serialization strategies that the serializer they are using for their API. For example, if you are using KotlinX
serialization in order to convert kotlin fields from camel case to snake case, you could leverage
the `KotlinXSchemaConfigurator` in order to instruct Kompendium on how to serialize values properly.

```kotlin
private fun Application.mainModule() {
  install(ContentNegotiation) {
    json(Json {
      serializersModule = KompendiumSerializersModule.module
      encodeDefaults = true
      explicitNulls = false
    })
  }
  install(NotarizedApplication()) {
    spec = baseSpec
    // Adds support for @Transient and @SerialName
    // If you are not using them this is not required.
    schemaConfigurator = KotlinXSchemaConfigurator()
  }
}
```
