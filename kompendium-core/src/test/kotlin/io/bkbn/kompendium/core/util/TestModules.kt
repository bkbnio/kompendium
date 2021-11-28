package io.bkbn.kompendium.core.util

import io.bkbn.kompendium.core.Bibbity
import io.bkbn.kompendium.core.ComplexGibbit
import io.bkbn.kompendium.core.DefaultParameter
import io.bkbn.kompendium.core.ExceptionResponse
import io.bkbn.kompendium.core.Gibbity
import io.bkbn.kompendium.core.Mysterious
import io.bkbn.kompendium.core.Notarized.notarizedDelete
import io.bkbn.kompendium.core.Notarized.notarizedException
import io.bkbn.kompendium.core.Notarized.notarizedGet
import io.bkbn.kompendium.core.Notarized.notarizedPost
import io.bkbn.kompendium.core.Notarized.notarizedPut
import io.bkbn.kompendium.core.SimpleGibbit
import io.bkbn.kompendium.core.TestNested
import io.bkbn.kompendium.core.TestRequest
import io.bkbn.kompendium.core.TestResponse
import io.bkbn.kompendium.core.TestResponseInfo
import io.bkbn.kompendium.core.metadata.MethodInfo
import io.bkbn.kompendium.core.metadata.RequestInfo
import io.bkbn.kompendium.core.metadata.ResponseInfo
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.serialization.json

fun Application.kotlinxConfigModule() {
  install(ContentNegotiation) {
    json()
  }
}

fun Application.statusPageModule() {
  install(StatusPages) {
    notarizedException<Exception, ExceptionResponse>(
      info = ResponseInfo(
        HttpStatusCode.BadRequest,
        "Bad Things Happened"
      )
    ) {
      call.respond(HttpStatusCode.BadRequest, ExceptionResponse("Why you do dis?"))
    }
  }
}

fun Application.statusPageMultiExceptions() {
  install(StatusPages) {
    notarizedException<AccessDeniedException, Unit>(
      info = ResponseInfo(HttpStatusCode.Forbidden, "New API who dis?")
    ) {
      call.respond(HttpStatusCode.Forbidden)
    }
    notarizedException<Exception, ExceptionResponse>(
      info = ResponseInfo(
        HttpStatusCode.BadRequest,
        "Bad Things Happened"
      )
    ) {
      call.respond(HttpStatusCode.BadRequest, ExceptionResponse("Why you do dis?"))
    }
  }
}

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

fun Application.emptyGet() {
  routing {
    route("/test/empty") {
      notarizedGet(TestResponseInfo.trulyEmptyTestGetInfo) {
        call.respond(HttpStatusCode.OK)
      }
    }
  }
}

fun Application.withExamples() {
  routing {
    route("/test/examples") {
      notarizedPost(
        info = MethodInfo.PostInfo<Unit, TestRequest, TestResponse>(
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
        info = MethodInfo.GetInfo<DefaultParameter, TestResponse>(
          summary = "Testing Default Params",
          description = "Should have a default parameter value"
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
      notarizedGet(TestResponseInfo.emptyTestGetInfo) {
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
