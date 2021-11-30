package io.bkbn.kompendium.playground

import io.bkbn.kompendium.core.Notarized.notarizedDelete
import io.bkbn.kompendium.core.Notarized.notarizedGet
import io.bkbn.kompendium.core.Notarized.notarizedPost
import io.bkbn.kompendium.core.Notarized.notarizedPut
import io.bkbn.kompendium.core.metadata.ResponseInfo
import io.bkbn.kompendium.core.routes.openApi
import io.bkbn.kompendium.core.routes.redoc
import io.bkbn.kompendium.playground.PlaygroundToC.testAuthenticatedSingleGetInfo
import io.bkbn.kompendium.playground.PlaygroundToC.testCustomOverride
import io.bkbn.kompendium.playground.PlaygroundToC.testGetWithExamples
import io.bkbn.kompendium.playground.PlaygroundToC.testIdGetInfo
import io.bkbn.kompendium.playground.PlaygroundToC.testPostWithExamples
import io.bkbn.kompendium.playground.PlaygroundToC.testSingleDeleteInfo
import io.bkbn.kompendium.playground.PlaygroundToC.testSingleGetInfo
import io.bkbn.kompendium.playground.PlaygroundToC.testSingleGetInfoWithThrowable
import io.bkbn.kompendium.playground.PlaygroundToC.testSinglePostInfo
import io.bkbn.kompendium.playground.PlaygroundToC.testSinglePutInfo
import io.bkbn.kompendium.playground.PlaygroundToC.testUndeclaredFields
import io.bkbn.kompendium.swagger.swaggerUI
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.basic
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.webjars.Webjars
import org.joda.time.DateTime

fun main() {
  embeddedServer(
    Netty,
    port = 8081,
    module = Application::mainModule
  ).start(wait = true)
}

private var featuresInstalled = false

private fun Application.configModule() {
  if (!featuresInstalled) {
    install(ContentNegotiation) {
      json()
    }
    install(Authentication) {
      basic("basic") {
        realm = "Ktor Server"
        validate { credentials ->
          if (credentials.name == credentials.password) {
            UserIdPrincipal(credentials.name)
          } else {
            null
          }
        }
      }
    }
    install(Webjars)
    install(StatusPages) {
      exception<Exception> { _ ->
        call.respond(HttpStatusCode.BadRequest, ExceptionResponse("Why you do dis?"))
      }
    }
    featuresInstalled = true
  }
}

private fun Application.mainModule() {
  configModule()
  routing {
    openApi(oas)
    redoc(oas)
    swaggerUI()
    route("/potato/spud") {
      notarizedGet(testGetWithExamples) {
        call.respond(HttpStatusCode.OK)
      }
      notarizedPost(testPostWithExamples) {
        call.respond(HttpStatusCode.Created, ExampleResponse("hey"))
      }
    }
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
        notarizedPost(testSinglePostInfo) {
          call.respondText("test post")
        }
        notarizedPut(testSinglePutInfo) {
          call.respondText { "hey" }
        }
        notarizedDelete(testSingleDeleteInfo) {
          call.respondText { "heya" }
        }
      }
      route("custom_override") {
        notarizedGet(testCustomOverride) {
          call.respondText { DateTime.now().toString() }
        }
      }
      authenticate("basic") {
        route("/authenticated/single") {
          notarizedGet(testAuthenticatedSingleGetInfo) {
            call.respond(HttpStatusCode.OK)
          }
        }
      }
    }
    route("/error") {
      notarizedGet(testSingleGetInfoWithThrowable) {
        error("bad things just happened")
      }
    }
    route("/undeclared") {
      notarizedGet(testUndeclaredFields) {
        call.respondText { "hi" }
      }
    }
  }
}
