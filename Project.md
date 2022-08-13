# Kompendium

Welcome to Kompendium, the straight-forward, non-invasive OpenAPI generator for Ktor.  

## How to install

Kompendium publishes all releases to Maven Central. As such, using the release versions of `Kompendium` is as simple as
declaring it as an implementation dependency in your `build.gradle.kts`

```kotlin
repositories {
  mavenCentral()
}

dependencies {
  implementation("io.bkbn:kompendium-core:latest.release")
}
```

In addition to publishing releases to Maven Central, a snapshot version gets published to GitHub Packages on every merge
to `main`. These can be consumed by adding the repository to your gradle build file. Instructions can be
found [here](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry#using-a-published-package)

## Setting up Kompendium

Kompendium's core features are comprised of a singular application level plugin and a collection of route level plugins. 
The former sets up your OpenApi spec along with various cross-route metadata and overrides such as custom types (useful
for things like datetime libraries)

### `NotarizedApplication` plugin

The notarized application plugin is installed at (surprise!) the app level

```kotlin
private fun Application.mainModule() {
  install(NotarizedApplication()) {
    spec = OpenApiSpec(
      // spec details go here ...
    )
  }
}
```

### `NotarizedRoute` plugin

Notarized routes take advantage of Ktor 2's [route specific plugin](https://ktor.io/docs/plugins.html#install-route)
feature.  This allows us to take individual routes, document them, and feed them back in to the application level plugin. 

This also allows you to adopt Kompendium incrementally. Individual routes can be documented at your leisure, and is purely
additive, meaning that you do not need to modify existing code to get documentation working, you just need new code! 

Non-invasive FTW 🚀

Documenting a simple `GET` endpoint would look something like this

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


route("/{id}") {
  documentation()
  get {
    call.respond(HttpStatusCode.OK, ExampleResponse(true))
  }
}
```

Full details on application and route notarization can be found in the `core` module
