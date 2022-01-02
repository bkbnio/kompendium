package io.bkbn.kompendium.core.util

import io.bkbn.kompendium.core.Notarized.notarizedDelete
import io.bkbn.kompendium.core.Notarized.notarizedGet
import io.bkbn.kompendium.core.Notarized.notarizedPost
import io.bkbn.kompendium.core.Notarized.notarizedPut
import io.bkbn.kompendium.core.fixtures.Bibbity
import io.bkbn.kompendium.core.fixtures.ComplexGibbit
import io.bkbn.kompendium.core.fixtures.DefaultParameter
import io.bkbn.kompendium.core.fixtures.Gibbity
import io.bkbn.kompendium.core.fixtures.Mysterious
import io.bkbn.kompendium.core.fixtures.SimpleGibbit
import io.bkbn.kompendium.core.fixtures.TestFieldOverride
import io.bkbn.kompendium.core.fixtures.TestHelpers.DEFAULT_TEST_ENDPOINT
import io.bkbn.kompendium.core.fixtures.TestNested
import io.bkbn.kompendium.core.fixtures.TestRequest
import io.bkbn.kompendium.core.fixtures.TestResponse
import io.bkbn.kompendium.core.fixtures.TestResponseInfo
import io.bkbn.kompendium.core.fixtures.TestResponseInfo.defaultField
import io.bkbn.kompendium.core.fixtures.TestResponseInfo.defaultParam
import io.bkbn.kompendium.core.fixtures.TestResponseInfo.nullableField
import io.bkbn.kompendium.core.fixtures.TestResponseInfo.requiredParam
import io.bkbn.kompendium.core.metadata.RequestInfo
import io.bkbn.kompendium.core.metadata.ResponseInfo
import io.bkbn.kompendium.core.metadata.method.GetInfo
import io.bkbn.kompendium.core.metadata.method.PostInfo
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.route
import io.ktor.routing.routing

fun Application.notarizedGetWithNotarizedException() {
  routing {
    route("/test") {
      notarizedGet(TestResponseInfo.testGetWithException) {
        error("something terrible has happened!")
      }
    }
  }
}

fun Application.notarizedGetWithMultipleThrowables() {
  routing {
    route("/test") {
      notarizedGet(TestResponseInfo.testGetWithMultipleExceptions) {
        error("something terrible has happened!")
      }
    }
  }
}

fun Application.notarizedGetWithPolymorphicErrorResponse() {
  routing {
    route(DEFAULT_TEST_ENDPOINT) {
      notarizedGet(TestResponseInfo.testGetWithPolymorphicException) {
        error("something terrible has happened!")
      }
    }
  }
}

fun Application.notarizedGetWithGenericErrorResponse() {
  routing {
    route(DEFAULT_TEST_ENDPOINT) {
      notarizedGet(TestResponseInfo.testGetWithGenericException) {
        error("something terrible has happened!")
      }
    }
  }
}

fun Application.notarizedGetModule() {
  routing {
    route("/test") {
      notarizedGet(TestResponseInfo.testGetInfo) {
        call.respondText { "hey dude ‼️ congratz on the get request" }
      }
    }
  }
}

fun Application.notarizedPostModule() {
  routing {
    route("/test") {
      notarizedPost(TestResponseInfo.testPostInfo) {
        call.respondText { "hey dude ✌️ congratz on the post request" }
      }
    }
  }
}

fun Application.notarizedDeleteModule() {
  routing {
    route("/test") {
      notarizedDelete(TestResponseInfo.testDeleteInfo) {
        call.respond(HttpStatusCode.NoContent)
      }
    }
  }
}

fun Application.notarizedPutModule() {
  routing {
    route("/test") {
      notarizedPut(TestResponseInfo.testPutInfoAlso) {
        call.respondText { "hey pal 🌝 whatcha doin' here?" }
      }
    }
  }
}

fun Application.pathParsingTestModule() {
  routing {
    route("/this") {
      route("/is") {
        route("/a") {
          route("/complex") {
            route("path") {
              route("with/an/{id}") {
                notarizedGet(TestResponseInfo.testGetInfo) {
                  call.respondText { "Aww you followed this whole route 🥺" }
                }
              }
            }
          }
        }
      }
    }
  }
}

fun Application.rootModule() {
  routing {
    route("/") {
      notarizedGet(TestResponseInfo.testGetInfo) {
        call.respondText { "☎️🏠🌲" }
      }
    }
  }
}

fun Application.nestedUnderRootModule() {
  routing {
    route("/") {
      route("/testerino") {
        notarizedGet(TestResponseInfo.testGetInfo) {
          call.respondText { "🤔🔥" }
        }
      }
    }
  }
}

fun Application.trailingSlash() {
  routing {
    route("/test") {
      route("/") {
        notarizedGet(TestResponseInfo.testGetInfo) {
          call.respondText { "🙀👾" }
        }
      }
    }
  }
}

fun Application.returnsList() {
  routing {
    route("/test") {
      notarizedGet(TestResponseInfo.testGetInfoAgain) {
        call.respondText { "hey dude ur doing amazing work!" }
      }
    }
  }
}

fun Application.complexType() {
  routing {
    route("/test") {
      notarizedPut(TestResponseInfo.testPutInfo) {
        call.respondText { "heya" }
      }
    }
  }
}

fun Application.primitives() {
  routing {
    route("/test") {
      notarizedPut(TestResponseInfo.testPutInfoAgain) {
        call.respondText { "heya" }
      }
    }
  }
}

fun Application.withExamples() {
  routing {
    route("/test/examples") {
      notarizedPost(
        info = PostInfo<Unit, TestRequest, TestResponse>(
          summary = "Example Parameters",
          description = "A test for setting parameter examples",
          requestInfo = RequestInfo(
            description = "Test",
            examples = mapOf(
              "one" to TestRequest(fieldName = TestNested(nesty = "hey"), b = 4.0, aaa = emptyList()),
              "two" to TestRequest(fieldName = TestNested(nesty = "hello"), b = 3.8, aaa = listOf(31324234))
            )
          ),
          responseInfo = ResponseInfo(
            status = HttpStatusCode.Created,
            description = "nice",
            examples = mapOf("test" to TestResponse(c = "spud"))
          ),
        )
      ) {
        call.respond(HttpStatusCode.OK)
      }
    }
  }
}

fun Application.withDefaultParameter() {
  routing {
    route("/test") {
      notarizedGet(
        info = GetInfo<DefaultParameter, TestResponse>(
          summary = "Testing Default Params",
          description = "Should have a default parameter value",
          responseInfo = ResponseInfo(
            HttpStatusCode.OK,
            "A good response"
          )
        )
      ) {
        call.respond(TestResponse("hey"))
      }
    }
  }
}

fun Application.withOperationId(){
  routing {
    route("/test") {
      notarizedGet(
        info = TestResponseInfo.testGetInfo.copy(operationId = "getTest")
      ){
        call.respond(HttpStatusCode.OK)
      }
    }
  }
}

fun Application.nonRequiredParamsGet() {
  routing {
    route("/test/optional") {
      notarizedGet(TestResponseInfo.testOptionalParams) {
        call.respond(HttpStatusCode.OK)
      }
    }
  }
}

fun Application.polymorphicResponse() {
  routing {
    route("/test/polymorphic") {
      notarizedGet(TestResponseInfo.polymorphicResponse) {
        call.respond(HttpStatusCode.OK, SimpleGibbit("hey"))
      }
    }
  }
}

fun Application.polymorphicCollectionResponse() {
  routing {
    route("/test/polymorphiclist") {
      notarizedGet(TestResponseInfo.polymorphicListResponse) {
        call.respond(HttpStatusCode.OK, listOf(SimpleGibbit("hi")))
      }
    }
  }
}

fun Application.polymorphicMapResponse() {
  routing {
    route("/test/polymorphicmap") {
      notarizedGet(TestResponseInfo.polymorphicMapResponse) {
        call.respond(HttpStatusCode.OK, listOf(SimpleGibbit("hi")))
      }
    }
  }
}

fun Application.polymorphicInterfaceResponse() {
  routing {
    route("/test/polymorphicmap") {
      notarizedGet(TestResponseInfo.polymorphicInterfaceResponse) {
        call.respond(HttpStatusCode.OK, listOf(SimpleGibbit("hi")))
      }
    }
  }
}

fun Application.genericPolymorphicResponse() {
  routing {
    route("/test/polymorphic") {
      notarizedGet(TestResponseInfo.genericPolymorphicResponse) {
        call.respond(HttpStatusCode.OK, Gibbity("hey"))
      }
    }
  }
}

fun Application.genericPolymorphicResponseMultipleImpls() {
  routing {
    route("/test/polymorphic") {
      notarizedGet(TestResponseInfo.genericPolymorphicResponse) {
        call.respond(HttpStatusCode.OK, Gibbity("hey"))
      }
    }
    route("/test/also/poly") {
      notarizedGet(TestResponseInfo.anotherGenericPolymorphicResponse) {
        call.respond(HttpStatusCode.OK, Bibbity("test", ComplexGibbit("nice", 1)))
      }
    }
  }
}

fun Application.undeclaredType() {
  routing {
    route("/test/polymorphic") {
      notarizedGet(TestResponseInfo.undeclaredResponseType) {
        call.respond(HttpStatusCode.OK, Mysterious("hi"))
      }
    }
  }
}

fun Application.headerParameter() {
  routing {
    route("/test/with_header") {
      notarizedGet(TestResponseInfo.headerParam) {
        call.respond(HttpStatusCode.OK, TestResponse("hi"))
      }
    }
  }
}

fun Application.simpleGenericResponse() {
  routing {
    route("/test/polymorphic") {
      notarizedGet(TestResponseInfo.genericResponse) {
        call.respond(HttpStatusCode.OK, Gibbity("hey"))
      }
    }
  }
}

fun Application.overrideFieldInfo() {
  routing {
    route("/test/field_override") {
      notarizedGet(TestResponseInfo.fieldOverride) {
        call.respond(HttpStatusCode.OK, TestFieldOverride(true))
      }
    }
  }
}

fun Application.constrainedIntInfo() {
  routing {
    route("/test/constrained_int") {
      notarizedGet(TestResponseInfo.minMaxInt) {
        call.respond(HttpStatusCode.OK, TestResponse("hi"))
      }
    }
  }
}

fun Application.requiredParameter() {
  routing {
    route("/test/required_param") {
      notarizedGet(requiredParam) {
        call.respond(HttpStatusCode.OK, TestResponse("hi"))
      }
    }
  }
}

fun Application.defaultParameter() {
  routing {
    route("/test/required_param") {
      notarizedGet(defaultParam) {
        call.respond(HttpStatusCode.OK, TestResponse("hi"))
      }
    }
  }
}

fun Application.defaultField() {
  routing {
    route("/test/required_param") {
      notarizedPost(defaultField) {
        call.respond(HttpStatusCode.OK, TestResponse("hi"))
      }
    }
  }
}

fun Application.nullableField() {
  routing {
    route("/test/required_param") {
      notarizedPost(nullableField) {
        call.respond(HttpStatusCode.OK, TestResponse("hi"))
      }
    }
  }
}
