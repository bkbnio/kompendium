Plugins are the lifeblood of Kompendium.

It starts with the `NotarizedApplication`, where Kompendium is instantiated and attached to the API. This is where spec
metadata is defined, custom types are defined, and more.

From there, a `NotarizedRoute` plugin is attached to each route you wish to document. This allows API documentation to
be an iterative process. Each route you notarize will be picked up and injected into the OpenAPI spec that Kompendium
generates for you.

Finally, there is the `NotarizedLocations` plugin that allows you to leverage and document your usage of the
Ktor [Locations](https://ktor.io/docs/locations.html) API.
