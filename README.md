# Kompendium

## What is Kompendium

Kompendium is intended to be a minimally invasive OpenApi Specification generator for [Ktor](https://ktor.io). 
Minimally invasive meaning that users will use only Ktor native functions when implementing their API, and will 
supplement with Kompendium code in order to generate the appropriate spec. 

## How to install

Kompendium uses GitHub packages as its repository.  Installing with Gradle is pretty painless.  In your `build.gradle.kts`
add the following 

```kotlin
// 1 Setup a helper function to import any Github Repository Package
// This step is optional but I have a bunch of stuff stored on github so I find it useful 😄
fun RepositoryHandler.github(packageUrl: String) = maven { 
    name = "GithubPackages"
    url = uri(packageUrl)
    credentials { // TODO Not sure this is necessary for public repositories?
      username = java.lang.System.getenv("GITHUB_USER")
      password = java.lang.System.getenv("GITHUB_TOKEN")
    } 
}

// 2 Add the repo in question (in this case Kompendium)
repositories {
    github("https://maven.pkg.github.com/lg-backbone/kompendium")
}

// 3 Add the package like any normal dependency
dependencies { 
    implementation("org.leafygreens:kompendium-core:0.1.0-SNAPSHOT")
}

```

## In depth

### Warning 🚨
Kompendium is still under active development ⚠️ There are a number of yet-to-be-implemented features, including 

- Query and Path Parameters 🔍
- Tags 🏷
- Multiple Responses 📜
- Security Schemas 🔏
- Sealed Class / Polymorphic Support 😬  
- Validation / Enforcement (❓👀❓)

If you have a feature that is not listed here, please open an issue!

### Notarized Routes 

Kompendium introduces the concept of `notarized` HTTP methods.  That is, for all your `GET`, `POST`, `PUT`, and `DELETE`
operations, there is a corresponding `notarized` method.  These operations are strongly typed, and use reification for 
a lot of the class based reflection that powers Kompendium.  Generally speaking the three types that a `notarized` method
will consume are

- `TParam`: Held for future implementations, will allow Kompendium to register information on path and query parameters
- `TReq`: Used to build the object schema for a request body
- `TResp`: Used to build the object schema for a response body

`GET` and `DELETE` take `TParam` and `TResp` while `PUT` and `POST` take all three.

In keeping with minimal invasion, these extension methods all consume the same code block as a standard Ktor route method,
meaning that swapping in a default Ktor route and a Kompendium `notarized` route is as simple as a single method change.

### Supplemental Annotations

In general, Kompendium tries to limit the number of annotations that developers need to use in order to get an app 
integrated.  However, there are a couple areas that it made sense, at least for an MVP.  

Currently, there are three Kompendium annotations

- `KompendiumRequest`
- `KompendiumResponse`
- `KompendiumField`

These are aimed at offering modifications at the request, response, and field level respectively, and offer things such
as response status codes, field name overrides, and OpenApi metadata such as `description`.

## Examples

```kotlin
// Minimal API Example
fun Application.mainModule() {
  install(ContentNegotiation) {
    jackson()
  }
  routing {
    route("/test") {
      route("/{id}") {
        notarizedGet<ExampleParams, ExampleResponse>(testIdGetInfo) {
          call.respondText("get by id")
        }
      }
      route("/single") {
        notarizedGet<ExampleRequest, ExampleResponse>(testSingleGetInfo) {
          call.respondText("get single")
        }
        notarizedPost<ExampleParams, ExampleRequest, ExampleCreatedResponse>(testSinglePostInfo) {
          call.respondText("test post")
        }
        notarizedPut<ExampleParams, ExampleRequest, ExampleCreatedResponse>(testSinglePutInfo) {
          call.respondText { "hey" }
        }
        notarizedDelete<Unit, DeleteResponse>(testSingleDeleteInfo) {
          call.respondText { "heya" }
        }
      }
    }
    route("/openapi.json") {
      get {
        call.respond(openApiSpec.copy(
          info = OpenApiSpecInfo(
            title = "Test API",
            version = "1.3.3.7",
            description = "An amazing, fully-ish 😉 generated API spec"
          )
        ))
      }
    }
  }
}

// Ancillary Data 
data class ExampleParams(val a: String, val aa: Int)

data class ExampleNested(val nesty: String)

@KompendiumResponse(status = KompendiumHttpCodes.NO_CONTENT, "Entity was deleted successfully")
object DeleteResponse

@KompendiumRequest("Example Request")
data class ExampleRequest(
  @KompendiumField(name = "field_name")
  val fieldName: ExampleNested,
  val b: Double,
  val aaa: List<Long>
)

@KompendiumResponse(KompendiumHttpCodes.OK, "A Successful Endeavor")
data class ExampleResponse(val c: String)

@KompendiumResponse(KompendiumHttpCodes.CREATED, "Created Successfully")
data class ExampleCreatedResponse(val id: Int, val c: String)

object KompendiumTOC {
  val testIdGetInfo = MethodInfo("Get Test", "Test for getting", tags = setOf("test", "example", "get"))
  val testSingleGetInfo = MethodInfo("Another get test", "testing more")
  val testSinglePostInfo = MethodInfo("Test post endpoint", "Post your tests here!")
  val testSinglePutInfo = MethodInfo("Test put endpoint", "Put your tests here!")
  val testSingleDeleteInfo = MethodInfo("Test delete endpoint", "testing my deletes")
}
```

This example would output the following json spec https://gist.github.com/rgbrizzlehizzle/b9544922f2e99a2815177f8bdbf80668

## Limitations

### Kompendium as a singleton

Currently, Kompendium exists as a Kotlin object.  This comes with a couple perks, but a couple downsides.  Primarily,
it offers a seriously clean UX where the implementer doesn't need to worry about what instance to send data to. The main
drawback, however, is that you are limited to a single API per classpath.  

If this is a blocker, please open a GitHub issue, and we can start to think out solutions! 
