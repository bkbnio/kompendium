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

Obviously, our route documentation would not be very useful without a way to detail the operations that are available at
the specified route. These operations will take the metadata defined, along with any existing info present at the route
level detailed [above](#route-level-metadata). Together, this defines an OpenAPI path operation.

## Operation Builders

Each HTTP Operation (Get, Put Post, Patch, Delete, Head, Options) has its own `Builder` that Kompendium uses to define
the necessary information to associate with the detailed operation. For example, a simple `GET` request could be
defined as follows.

```kotlin
install(NotarizedRoute()) {
  get = GetInfo.builder {
    summary("Get ImportantDetail")
    description("Retrieves an Important Detail from the database")
    response {
      responseCode(HttpStatusCode.OK)
      responseType<TestResponse>()
      description("The Detail in Question")
    }
  }
}
```

## Operation Tags

Operation tags work much like route tags, except they only apply to the operation they are defined in.

## Operation Parameters

## Defining Request and Response Bodies
