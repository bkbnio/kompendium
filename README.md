# Kompendium

## What is Kompendium

Kompendium is intended to be a minimally invasive 
OpenApi Specification generator for 
[Ktor](https://ktor.io).
Minimally invasive meaning that users will use only 
Ktor native functions when implementing their API, 
and will supplement with Kompendium code in order 
to generate the appropriate spec. 

## Modules

TODO

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
        notarizedGet(testIdGetInfo) {
          call.respondText("get by id")
        }
      }
      route("/single") {
        notarizedGet(testSingleGetInfo) {
          call.respondText("get single")
        }
        notarizedPost<A, B, C>(testSinglePostInfo) {
          call.respondText("test post")
        }
        notarizedPut<A, B, D>(testSinglePutInfo) {
          call.respondText { "hey" }
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
```
