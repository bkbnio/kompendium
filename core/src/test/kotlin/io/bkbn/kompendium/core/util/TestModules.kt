package io.bkbn.kompendium.core.util

import io.bkbn.kompendium.core.fixtures.TestCreatedResponse
import io.bkbn.kompendium.core.fixtures.TestResponse
import io.bkbn.kompendium.core.fixtures.TestSimpleRequest
import io.bkbn.kompendium.core.metadata.DeleteInfo
import io.bkbn.kompendium.core.metadata.GetInfo
import io.bkbn.kompendium.core.metadata.PatchInfo
import io.bkbn.kompendium.core.metadata.PostInfo
import io.bkbn.kompendium.core.metadata.PutInfo
import io.bkbn.kompendium.core.plugin.NotarizedRoute
import io.bkbn.kompendium.json.schema.TypeDefinition
import io.bkbn.kompendium.oas.payload.Parameter
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

const val defaultPath = "/test/{a}"

val defaultParams = listOf(
  Parameter(
    name = "a",
    `in` = Parameter.Location.path,
    schema = TypeDefinition.STRING,
  ),
  Parameter(
    name = "aa",
    `in` = Parameter.Location.query,
    schema = TypeDefinition.INT
  )
)

fun Routing.notarizedGet() {
  route(defaultPath) {
    install(NotarizedRoute()) {
      path = defaultPath
      parameters = defaultParams
      get = GetInfo.builder {
        response {
          responseCode(HttpStatusCode.OK)
          responseType<TestResponse>()
          description("A Successful Endeavor")
        }
        summary("Another get test")
        description("testing more")
      }
    }
    get {
      call.respondText { "hey dude ‼️ congrats on the get request" }
    }
  }
}

fun Routing.notarizedPost() {
  route(defaultPath) {
    install(NotarizedRoute()) {
      path = defaultPath
      parameters = defaultParams
      post = PostInfo.builder {
        summary("Test post endpoint")
        description("Post your tests here!")
        request {
          requestType<TestSimpleRequest>()
          description("A Test request")
        }
        response {
          responseCode(HttpStatusCode.Created)
          responseType<TestCreatedResponse>()
          description("A Successful Endeavor")
        }
      }
    }
    post {
      call.respondText { "hey dude ‼️ congrats on the post request" }
    }
  }
}

fun Routing.notarizedPut() {
  route(defaultPath) {
    install(NotarizedRoute()) {
      path = defaultPath
      parameters = defaultParams
      put = PutInfo.builder {
        summary("Test post endpoint")
        description("Post your tests here!")
        request {
          requestType<TestSimpleRequest>()
          description("A Test request")
        }
        response {
          responseCode(HttpStatusCode.Created)
          responseType<TestCreatedResponse>()
          description("A Successful Endeavor")
        }
      }
    }
    put {
      call.respondText { "hey dude ‼️ congrats on the post request" }
    }
  }
}

fun Routing.notarizedDelete() {
  route(defaultPath) {
    install(NotarizedRoute()) {
      path = defaultPath
      parameters = defaultParams
      delete = DeleteInfo.builder {
        summary("Test delete endpoint")
        description("testing my deletes")
        response {
          responseCode(HttpStatusCode.NoContent)
          responseType<Unit>()
          description("A Successful Endeavor")
        }
      }
    }
  }
  delete {
    call.respond(HttpStatusCode.NoContent)
  }
}

fun Routing.notarizedPatch() {
  route(defaultPath) {
    install(NotarizedRoute()) {
      path = defaultPath
      parameters = defaultParams
      patch = PatchInfo.builder {
        summary("Test patch endpoint")
        description("patch your tests here!")
        request {
          description("A Test request")
          requestType<TestSimpleRequest>()
        }
        response {
          responseCode(HttpStatusCode.Created)
          responseType<TestCreatedResponse>()
          description("A Successful Endeavor")
        }
      }
    }
  }
}

//fun Application.notarizedPatchModule() {
//  routing {
//    route("/test") {
//      notarizedPatch(TestResponseInfo.testPatchInfo) {
//        call.respondText { "hey dude ✌️ congratz on the patch request" }
//      }
//    }
//  }
//}
//
//fun Application.notarizedHeadModule() {
//  routing {
//    route("/test") {
//      notarizedHead(TestResponseInfo.testHeadInfo) {
//        call.response.status(HttpStatusCode.OK)
//      }
//    }
//  }
//}
//
//fun Application.notarizedOptionsModule() {
//  routing {
//    route("/test") {
//      notarizedOptions(TestResponseInfo.testOptionsInfo) {
//        call.response.status(HttpStatusCode.OK)
//      }
//    }
//  }
//}
//
//fun Application.notarizedPutModule() {
//  routing {
//    route("/test") {
//      notarizedPut(TestResponseInfo.testPutInfoAlso) {
//        call.respondText { "hey pal 🌝 whatcha doin' here?" }
//      }
//    }
//  }
//}
//
//fun Application.pathParsingTestModule() {
//  routing {
//    route("/this") {
//      route("/is") {
//        route("/a") {
//          route("/complex") {
//            route("path") {
//              route("with/an/{id}") {
//                notarizedGet(TestResponseInfo.testGetInfo) {
//                  call.respondText { "Aww you followed this whole route 🥺" }
//                }
//              }
//            }
//          }
//        }
//      }
//    }
//  }
//}
//
//fun Application.rootModule() {
//  routing {
//    route("/") {
//      notarizedGet(TestResponseInfo.testGetInfo) {
//        call.respondText { "☎️🏠🌲" }
//      }
//    }
//  }
//}
//
//fun Application.nestedUnderRootModule() {
//  routing {
//    route("/") {
//      route("/testerino") {
//        notarizedGet(TestResponseInfo.testGetInfo) {
//          call.respondText { "🤔🔥" }
//        }
//      }
//    }
//  }
//}
//
//fun Application.trailingSlash() {
//  routing {
//    route("/test") {
//      route("/") {
//        notarizedGet(TestResponseInfo.testGetInfo) {
//          call.respondText { "🙀👾" }
//        }
//      }
//    }
//  }
//}
//
//fun Application.returnsList() {
//  routing {
//    route("/test") {
//      notarizedGet(TestResponseInfo.testGetInfoAgain) {
//        call.respondText { "hey dude ur doing amazing work!" }
//      }
//    }
//  }
//}
//
//fun Application.complexType() {
//  routing {
//    route("/test") {
//      notarizedPut(TestResponseInfo.testPutInfo) {
//        call.respondText { "heya" }
//      }
//    }
//  }
//}
//
//fun Application.primitives() {
//  routing {
//    route("/test") {
//      notarizedPut(TestResponseInfo.testPutInfoAgain) {
//        call.respondText { "heya" }
//      }
//    }
//  }
//}
//
//fun Application.withExamples() {
//  routing {
//    route("/test/examples") {
//      notarizedPost(
//        info = PostInfo<Unit, TestRequest, TestResponse>(
//          summary = "Example Parameters",
//          description = "A test for setting parameter examples",
//          requestInfo = RequestInfo(
//            description = "Test",
//            examples = mapOf(
//              "one" to TestRequest(fieldName = TestNested(nesty = "hey"), b = 4.0, aaa = emptyList()),
//              "two" to TestRequest(fieldName = TestNested(nesty = "hello"), b = 3.8, aaa = listOf(31324234))
//            )
//          ),
//          responseInfo = ResponseInfo(
//            status = HttpStatusCode.Created,
//            description = "nice",
//            examples = mapOf("test" to TestResponse(c = "spud"))
//          ),
//        )
//      ) {
//        call.respond(HttpStatusCode.OK)
//      }
//    }
//  }
//}
//
//fun Application.withDefaultParameter() {
//  routing {
//    route("/test") {
//      notarizedGet(
//        info = GetInfo<DefaultParameter, TestResponse>(
//          summary = "Testing Default Params",
//          description = "Should have a default parameter value",
//          responseInfo = ResponseInfo(
//            HttpStatusCode.OK,
//            "A good response"
//          )
//        )
//      ) {
//        call.respond(TestResponse("hey"))
//      }
//    }
//  }
//}
//
//fun Application.withOperationId() {
//  routing {
//    route("/test") {
//      notarizedGet(
//        info = TestResponseInfo.testGetInfo.copy(operationId = "getTest")
//      ) {
//        call.respond(HttpStatusCode.OK)
//      }
//    }
//  }
//}
//
//fun Application.nonRequiredParamsGet() {
//  routing {
//    route("/test/optional") {
//      notarizedGet(TestResponseInfo.testOptionalParams) {
//        call.respond(HttpStatusCode.OK)
//      }
//    }
//  }
//}
//
//fun Application.polymorphicResponse() {
//  routing {
//    route("/test/polymorphic") {
//      notarizedGet(TestResponseInfo.polymorphicResponse) {
//        call.respond(HttpStatusCode.OK, SimpleGibbit("hey"))
//      }
//    }
//  }
//}
//
//fun Application.polymorphicCollectionResponse() {
//  routing {
//    route("/test/polymorphiclist") {
//      notarizedGet(TestResponseInfo.polymorphicListResponse) {
//        call.respond(HttpStatusCode.OK, listOf(SimpleGibbit("hi")))
//      }
//    }
//  }
//}
//
//fun Application.polymorphicMapResponse() {
//  routing {
//    route("/test/polymorphicmap") {
//      notarizedGet(TestResponseInfo.polymorphicMapResponse) {
//        call.respond(HttpStatusCode.OK, listOf(SimpleGibbit("hi")))
//      }
//    }
//  }
//}
//
//fun Application.polymorphicInterfaceResponse() {
//  routing {
//    route("/test/polymorphicmap") {
//      notarizedGet(TestResponseInfo.polymorphicInterfaceResponse) {
//        call.respond(HttpStatusCode.OK, listOf(SimpleGibbit("hi")))
//      }
//    }
//  }
//}
//
//fun Application.genericPolymorphicResponse() {
//  routing {
//    route("/test/polymorphic") {
//      notarizedGet(TestResponseInfo.genericPolymorphicResponse) {
//        call.respond(HttpStatusCode.OK, Gibbity("hey"))
//      }
//    }
//  }
//}
//
//fun Application.genericPolymorphicResponseMultipleImpls() {
//  routing {
//    route("/test/polymorphic") {
//      notarizedGet(TestResponseInfo.genericPolymorphicResponse) {
//        call.respond(HttpStatusCode.OK, Gibbity("hey"))
//      }
//    }
//    route("/test/also/poly") {
//      notarizedGet(TestResponseInfo.anotherGenericPolymorphicResponse) {
//        call.respond(HttpStatusCode.OK, Bibbity("test", ComplexGibbit("nice", 1)))
//      }
//    }
//  }
//}
//
//fun Application.undeclaredType() {
//  routing {
//    route("/test/polymorphic") {
//      notarizedGet(TestResponseInfo.undeclaredResponseType) {
//        call.respond(HttpStatusCode.OK, Mysterious("hi"))
//      }
//    }
//  }
//}
//
//fun Application.headerParameter() {
//  routing {
//    route("/test/with_header") {
//      notarizedGet(TestResponseInfo.headerParam) {
//        call.respond(HttpStatusCode.OK, TestResponse("hi"))
//      }
//    }
//  }
//}
//
//fun Application.simpleGenericResponse() {
//  routing {
//    route("/test/polymorphic") {
//      notarizedGet(TestResponseInfo.genericResponse) {
//        call.respond(HttpStatusCode.OK, Gibbity("hey"))
//      }
//    }
//  }
//}
//
//fun Application.overrideFieldInfo() {
//  routing {
//    route("/test/field_override") {
//      notarizedGet(TestResponseInfo.fieldOverride) {
//        call.respond(HttpStatusCode.OK, TestFieldOverride(true))
//      }
//    }
//  }
//}
//
//fun Application.simpleRecursive() {
//  routing {
//    route("/test/simple_recursive") {
//      notarizedGet(TestResponseInfo.simpleRecursive) {
//        call.respond(HttpStatusCode.OK)
//      }
//    }
//  }
//}
//
//fun Application.nullableNestedObject() {
//  routing {
//    route("/nullable/nested") {
//      notarizedPost(nullableNested) {
//        call.respond(HttpStatusCode.OK)
//      }
//    }
//  }
//}
//
//fun Application.nullableEnumField() {
//  routing {
//    route("/nullable/enum") {
//      notarizedGet(TestResponseInfo.nullableEnumField) {
//        call.respond(HttpStatusCode.OK)
//      }
//    }
//  }
//}
//
//fun Application.constrainedIntInfo() {
//  routing {
//    route("/test/constrained_int") {
//      notarizedGet(TestResponseInfo.minMaxInt) {
//        call.respond(HttpStatusCode.OK, TestResponse("hi"))
//      }
//    }
//  }
//}
//
//fun Application.constrainedDoubleInfo() {
//  routing {
//    route("/test/constrained_int") {
//      notarizedGet(TestResponseInfo.minMaxDouble) {
//        call.respond(HttpStatusCode.OK, TestResponse("hi"))
//      }
//    }
//  }
//}
//
//fun Application.exclusiveMinMax() {
//  routing {
//    route("/test/constrained_int") {
//      notarizedGet(TestResponseInfo.exclusiveMinMax) {
//        call.respond(HttpStatusCode.OK, TestResponse("hi"))
//      }
//    }
//  }
//}
//
//fun Application.requiredParameter() {
//  routing {
//    route("/test/required_param") {
//      notarizedGet(requiredParam) {
//        call.respond(HttpStatusCode.OK, TestResponse("hi"))
//      }
//    }
//  }
//}
//
//fun Application.defaultParameter() {
//  routing {
//    route("/test/required_param") {
//      notarizedGet(defaultParam) {
//        call.respond(HttpStatusCode.OK, TestResponse("hi"))
//      }
//    }
//  }
//}
//
//fun Application.defaultField() {
//  routing {
//    route("/test/required_param") {
//      notarizedPost(defaultField) {
//        call.respond(HttpStatusCode.OK, TestResponse("hi"))
//      }
//    }
//  }
//}
//
//fun Application.nullableField() {
//  routing {
//    route("/test/required_param") {
//      notarizedPost(nullableField) {
//        call.respond(HttpStatusCode.OK, TestResponse("hi"))
//      }
//    }
//  }
//}
//
//fun Application.formattedParam() {
//  routing {
//    route("/test/required_param") {
//      notarizedGet(formattedParam) {
//        call.respond(HttpStatusCode.OK, TestResponse("hi"))
//      }
//    }
//  }
//}
//
//fun Application.minMaxString() {
//  routing {
//    route("/test/required_param") {
//      notarizedGet(minMaxString) {
//        call.respond(HttpStatusCode.OK, TestResponse("hi"))
//      }
//    }
//  }
//}
//
//fun Application.regexString() {
//  routing {
//    route("/test/required_param") {
//      notarizedGet(regexString) {
//        call.respond(HttpStatusCode.OK, TestResponse("hi"))
//      }
//    }
//  }
//}
//
//fun Application.dateTimeString() {
//  routing {
//    route("/test/date_time_format") {
//      notarizedGet(dateTimeString) {
//        call.respond(HttpStatusCode.OK, DateTimeString(Instant.now()))
//      }
//    }
//  }
//}
//
//fun Application.minMaxArray() {
//  routing {
//    route("/test/required_param") {
//      notarizedGet(TestResponseInfo.minMaxArray) {
//        call.respond(HttpStatusCode.OK, TestResponse("hi"))
//      }
//    }
//  }
//}
//
//fun Application.uniqueArray() {
//  routing {
//    route("/test/required_param") {
//      notarizedGet(TestResponseInfo.uniqueArray) {
//        call.respond(HttpStatusCode.OK, TestResponse("hi"))
//      }
//    }
//  }
//}
//
//fun Application.multipleOfInt() {
//  routing {
//    route("/test/required_param") {
//      notarizedGet(TestResponseInfo.multipleOfInt) {
//        call.respond(HttpStatusCode.OK, TestResponse("hi"))
//      }
//    }
//  }
//}
//
//fun Application.multipleOfDouble() {
//  routing {
//    route("/test/required_param") {
//      notarizedGet(TestResponseInfo.multipleOfDouble) {
//        call.respond(HttpStatusCode.OK, TestResponse("hi"))
//      }
//    }
//  }
//}
//
//fun Application.freeFormField() {
//  routing {
//    route("/test/required_param") {
//      notarizedGet(TestResponseInfo.freeFormField) {
//        call.respond(HttpStatusCode.OK, TestResponse("hi"))
//      }
//    }
//  }
//}
//
//fun Application.freeFormObject() {
//  routing {
//    route("/test/required_param") {
//      notarizedGet(TestResponseInfo.freeFormObject) {
//        call.respond(HttpStatusCode.OK, TestResponse("hi"))
//      }
//    }
//  }
//}
//
//fun Application.minMaxFreeForm() {
//  routing {
//    route("/test/required_param") {
//      notarizedGet(TestResponseInfo.minMaxFreeForm) {
//        call.respond(HttpStatusCode.OK, TestResponse("hi"))
//      }
//    }
//  }
//}
//
//fun Application.exampleParams() {
//  routing {
//    route("/test/{a}") {
//      notarizedGet(TestResponseInfo.exampleParams) {
//        call.respondText { "Hi 🌊" }
//      }
//    }
//  }
//}
//
//fun Application.formattedType() {
//  routing {
//    route("/test/formatted_type") {
//      notarizedPost(TestResponseInfo.formattedArrayItemType) {
//        call.respond(HttpStatusCode.OK, TestResponse("hi"))
//      }
//    }
//  }
//}
