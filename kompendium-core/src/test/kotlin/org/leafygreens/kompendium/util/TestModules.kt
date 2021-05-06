package org.leafygreens.kompendium.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.route
import io.ktor.routing.routing
import org.leafygreens.kompendium.Notarized.notarizedDelete
import org.leafygreens.kompendium.Notarized.notarizedException
import org.leafygreens.kompendium.Notarized.notarizedGet
import org.leafygreens.kompendium.Notarized.notarizedPost
import org.leafygreens.kompendium.Notarized.notarizedPut
import org.leafygreens.kompendium.models.meta.MethodInfo
import org.leafygreens.kompendium.models.meta.RequestInfo
import org.leafygreens.kompendium.models.meta.ResponseInfo

fun Application.configModule() {
  install(ContentNegotiation) {
    jackson {
      enable(SerializationFeature.INDENT_OUTPUT)
      setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }
  }
}

fun Application.statusPageModule() {
  install(StatusPages) {
    notarizedException<Exception, ExceptionResponse>(info = ResponseInfo(400, "Bad Things Happened")) {
      call.respond(HttpStatusCode.BadRequest, ExceptionResponse("Why you do dis?"))
    }
  }
}

fun Application.statusPageMultiExceptions() {
  install(StatusPages) {
    notarizedException<AccessDeniedException, Unit>(info = ResponseInfo(403, "New API who dis?")) {
      call.respond(HttpStatusCode.Forbidden)
    }
    notarizedException<Exception, ExceptionResponse>(info = ResponseInfo(400, "Bad Things Happened")) {
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
            status = 201,
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

fun Application.nonRequiredParamsGet() {
  routing {
    route("/test/optional") {
      notarizedGet(TestResponseInfo.emptyTestGetInfo) {
        call.respond(HttpStatusCode.OK)
      }
    }
  }
}
