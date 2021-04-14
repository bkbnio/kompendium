# Kompendium

## What is Kompendium

Kompendium is intended to be a minimally invasive 
OpenApi Specification generator for 
[Ktor](https://ktor.io).
Minimally invasive meaning that users will use only 
Ktor native functions when implementing their API, 
and will supplement with Kompendium code in order 
to generate the appropriate spec. 

## In depth

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
