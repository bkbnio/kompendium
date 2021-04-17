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
integrated.   

Currently, there is only a single Kompendium annotation

- `KompendiumField`

The intended purpose is to offer field level overrides such as naming conventions (ie snake instead of camel).

## Examples

The following source code can be found in the `kompendium-playground` module

```kotlin
// Minimal API Example
fun main() {
  embeddedServer(
    Netty,
    port = 8081,
    module = Application::mainModule
  ).start(wait = true)
}

data class ExampleParams(val a: String, val aa: Int)

data class ExampleNested(val nesty: String)

object DeleteResponse

data class ExampleRequest(
  @KompendiumField(name = "field_name")
  val fieldName: ExampleNested,
  val b: Double,
  val aaa: List<Long>
)

data class ExampleResponse(val c: String)

data class ExampleCreatedResponse(val id: Int, val c: String)

object KompendiumTOC {
  val testIdGetInfo = MethodInfo(
    summary = "Get Test",
    description = "Test for the getting",
    tags = setOf("test", "sample", "get"),
    responseInfo = ResponseInfo(
      status = KompendiumHttpCodes.OK,
      description = "Returns sample info"
    )
  )
  val testSingleGetInfo = MethodInfo(
    summary = "Another get test",
    description = "testing more",
    tags = setOf("anotherTest", "sample"),
    responseInfo = ResponseInfo(
      status = KompendiumHttpCodes.OK,
      description = "Returns a different sample"
    )
  )
  val testSinglePostInfo = MethodInfo(
    summary = "Test post endpoint",
    description = "Post your tests here!",
    requestInfo = RequestInfo(
      description = "Simple request body"
    ),
    responseInfo = ResponseInfo(
      status = KompendiumHttpCodes.CREATED,
      description = "Worlds most complex response"
    )
  )
  val testSinglePutInfo = MethodInfo(
    summary = "Test put endpoint",
    description = "Put your tests here!",
    requestInfo = RequestInfo(
      description = "Info needed to perform this put request"
    ),
    responseInfo = ResponseInfo(
      status = KompendiumHttpCodes.CREATED,
      description = "What we give you when u do the puts"
    )
  )
  val testSingleDeleteInfo = MethodInfo(
    summary = "Test delete endpoint",
    description = "testing my deletes",
    responseInfo = ResponseInfo(
      status = KompendiumHttpCodes.NO_CONTENT,
      description = "Signifies that your item was deleted succesfully"
    )
  )
}

fun Application.mainModule() {
  install(ContentNegotiation) {
    jackson {
      enable(SerializationFeature.INDENT_OUTPUT)
      setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }
  }
  routing {
    openApi()
    redoc()
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
  }
}

fun Routing.openApi() {
  route("/openapi.json") {
    get {
      call.respond(
        openApiSpec.copy(
          info = OpenApiSpecInfo(
            title = "Test API",
            version = "1.33.7",
            description = "An amazing, fully-ish 😉 generated API spec",
            termsOfService = URI("https://example.com"),
            contact = OpenApiSpecInfoContact(
              name = "Homer Simpson",
              email = "chunkylover53@aol.com",
              url = URI("https://gph.is/1NPUDiM")
            ),
            license = OpenApiSpecInfoLicense(
              name = "MIT",
              url = URI("https://github.com/lg-backbone/kompendium/blob/main/LICENSE")
            )
          ),
          servers = mutableListOf(
            OpenApiSpecServer(
              url = URI("https://myawesomeapi.com"),
              description = "Production instance of my API"
            ),
            OpenApiSpecServer(
              url = URI("https://staging.myawesomeapi.com"),
              description = "Where the fun stuff happens"
            )
          )
        )
      )
    }
  }
}

fun Routing.redoc() {
  route("/docs") {
    get {
      call.respondHtml {
        head {
          title {
            +"${openApiSpec.info.title}"
          }
          meta {
            charset = "utf-8"
          }
          meta {
            name = "viewport"
            content = "width=device-width, initial-scale=1"
          }
          link {
            href = "https://fonts.googleapis.com/css?family=Montserrat:300,400,700|Roboto:300,400,700"
            rel = "stylesheet"
          }
          style {
            unsafe {
              raw("body { margin: 0; padding: 0; }")
            }
          }
        }
        body {
          // TODO needs to mirror openApi route
          unsafe { +"<redoc spec-url='/openapi.json'></redoc>" }
          script {
            src = "https://cdn.jsdelivr.net/npm/redoc@next/bundles/redoc.standalone.js"
          }
        }
      }
    }
  }
}
```

This example would output the following json spec https://gist.github.com/rgbrizzlehizzle/b9544922f2e99a2815177f8bdbf80668

## Limitations

### Kompendium as a singleton

Currently, Kompendium exists as a Kotlin object.  This comes with a couple perks, but a couple downsides.  Primarily,
it offers a seriously clean UX where the implementer doesn't need to worry about what instance to send data to. The main
drawback, however, is that you are limited to a single API per classpath.  

If this is a blocker, please open a GitHub issue, and we can start to think out solutions! 
