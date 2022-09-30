The `NotarizedRoute` plugin is the method by which you define the functionality and metadata for an individual route of
your API.

# Route Level Metadata

## Route Tags

OpenAPI uses the concept of tags in order to logically group operations. Tags defined at the `NotarizedRoute` level will
be applied to _all_ operations defined in this route. In order to define tags for a specific operation,
see [below](#operation-tags).

```kotlin
private fun Route.documentation() {
  install(NotarizedRoute()) {
    tags = setOf("User")
    // will apply the User tag to all operations defined below
  }
}
```

## Route Parameters

Parameters can be defined at the route level. Doing so will assign the parameters to _all_ operations defined in this
route. In order to define parameters for a specific operation, see [below](#operation-parameters).

```kotlin
private fun Route.documentation() {
  install(NotarizedRoute()) {
    parameters = listOf(
      Parameter(
        name = "id",
        `in` = Parameter.Location.path,
        schema = TypeDefinition.STRING
      )
    )
    // Will apply the id parameter to all operations defined below
  }
}
```

# Defining Operations

TODO

## Operation Tags

## Operation Parameters

## Defining Request and Response Bodies
