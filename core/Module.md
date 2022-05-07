# Module kompendium-core

This is where the magic happens. This module houses all the reflective goodness that powers Kompendium.

It is also the only mandatory client-facing module for a basic setup.

# Package io.bkbn.kompendium.core

The root package contains several objects that power Kompendium, including the Kompendium Ktor Plugin, route
notarization methods, and the reflection engine that analyzes method info type parameters.

## Plugin

The Kompendium plugin is an extremely light-weight plugin, with only a couple areas of customization.

### Serialization

Kompendium relies on your API to provide a properly-configured `ContentNegotiator` in order to convert the `OpenApiSpec`
into JSON. The advantage to this approach is that all of your data classes will be serialized precisely how you define.
The downside is that issues could exist in serialization frameworks that have not been tested. At the moment, Jackson,
Gson and KotlinX serialization have all been tested. If you run into any serialization issues, particularly with a
serializer not listed above, please open an issue on GitHub 🙏

Note for Kotlinx ⚠️

You will need to include the `SerializersModule` provided in `KompendiumSerializersModule` in order to serialize 
any provided defaults.  This comes down to how Kotlinx expects users to handle serializing `Any`.  Essentially, this 
serializer module will convert any `Any` serialization to be `Contextual`.  This is pretty hacky, but seemed to be the
only way to get Kotlinx to play nice with serializing `Any`.  If you come up with a better solution, definitely go ahead
and open up a PR!

## Notarization

Central to Kompendium is the concept of notarization.

Notarizing a route is the mechanism by which Kompendium analyzes your route types, along with provided metadata, and
converts to the expected OpenAPI format.

Before jumping into notarization, lets first look at a standard Ktor route

```kotlin
routing {
  get {
    call.respond(HttpStatusCode.OK, BasicResponse(c = UUID.randomUUID().toString()))
  }
}
```

Now, let's compare this to the same functionality, but notarized using Kompendium

```kotlin
routing {
  notarizedGet(simpleGetExample) {
    call.respond(HttpStatusCode.OK, BasicResponse(c = UUID.randomUUID().toString()))
  }
}
```

Pretty simple huh. But hold on... what is this `simpleGetExample`? How can I know that it is so "simple". Let's take a
look

```kotlin
val simpleGetExample = GetInfo<Unit, BasicResponse>(
  summary = "Simple, Documented GET Request",
  description = "This is to showcase just how easy it is to document your Ktor API!",
  responseInfo = ResponseInfo(
    status = HttpStatusCode.OK,
    description = "This means everything went as expected!",
    examples = mapOf("demo" to BasicResponse(c = "52c099d7-8642-46cc-b34e-22f39b923cf4"))
  ),
  tags = setOf("Simple")
)
```

See, not so bad 😄 `GetInfo<*,*>` is an implementation of `MethodInfo<TParam, TResp>`, a sealed interface designed to
encapsulate all the metadata required for documenting an API route. Kompendium leverages this data, along with the
provided type parameters `TParam` and `TResp` to construct the full OpenAPI Specification for your route.

Additionally, just as a backup, each notarization method includes a "post-processing' hook that will allow you to have
final say in the generated route info prior to being attached to the spec. This can be accessed via the optional
parameter

```kotlin
routing {
  notarizedGet(simpleGetExample, postProcess = { spec -> spec }) {
    call.respond(HttpStatusCode.OK, BasicResponse(c = UUID.randomUUID().toString()))
  }
}
```

This should only be used in _extremely_ rare scenarios, but it is nice to know it is there if you need it.

# Package io.bkbn.kompendium.core.metadata

Houses all interfaces and types related to describing route metadata.

# Package io.bkbn.kompendium.core.parser

Responsible for the parse of method information. Base implementation is an interface to support extensibility as shown
in the `kompendium-locations` module.

# Package io.bkbn.kompendium.core.routes

Houses any routes provided by the core module.  At the moment the only supported route is to enable ReDoc support.

# Package io.bkbn.kompendium.core.util

Collection of utility functions used by Kompendium
