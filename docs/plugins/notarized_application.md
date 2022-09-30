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

TODO

## Custom Types

TODO

## Schema Configurator

TODO
