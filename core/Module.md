# Module kompendium-core

This is where the magic happens. This module houses all the reflective goodness that powers Kompendium.

It is also the only mandatory client-facing module for a basic setup.

# Package io.bkbn.kompendium.core

## Plugins

As mentioned in the root documentation, there are two core Kompendium plugins.

1. The application level plugin that handles app level metadata, configuring up your OpenAPI spec, managing custom data
   types, etc.
2. The route level plugin, which is how users declare the documentation for the given route. It _must_ be installed on
   every route you wish to document

### Serialization

Kompendium relies on your API to provide a properly-configured `ContentNegotiator` in order to convert the `OpenApiSpec`
into JSON. The advantage to this approach is that all of your data classes will be serialized precisely how you define.
The downside is that issues could exist in serialization frameworks that have not been tested. At the moment, Jackson,
Gson and KotlinX serialization have all been tested. If you run into any serialization issues, particularly with a
serializer not listed above, please open an issue on GitHub 🙏

Note for Kotlinx ⚠️

You will need to include the `SerializersModule` provided in `KompendiumSerializersModule` in order to serialize
any provided defaults. This comes down to how Kotlinx expects users to handle serializing `Any`. Essentially, this
serializer module will convert any `Any` serialization to be `Contextual`. This is pretty hacky, but seemed to be the
only way to get Kotlinx to play nice with serializing `Any`. If you come up with a better solution, definitely go ahead
and open up a PR!

## NotarizedApplication

TODO

## NotarizedRoute

TODO 

# Package io.bkbn.kompendium.core.metadata

Houses all interfaces and types related to describing route metadata.

# Package io.bkbn.kompendium.core.routes

Houses any routes provided by the core module. At the moment the only supported route is to enable ReDoc support.

# Package io.bkbn.kompendium.core.util

Collection of utility functions used by Kompendium
