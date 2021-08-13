package io.bkbn.kompendium.playground

import io.bkbn.kompendium.Kompendium
import io.bkbn.kompendium.Notarized.notarizedDelete
import io.bkbn.kompendium.Notarized.notarizedException
import io.bkbn.kompendium.Notarized.notarizedGet
import io.bkbn.kompendium.Notarized.notarizedPost
import io.bkbn.kompendium.Notarized.notarizedPut
import io.bkbn.kompendium.auth.KompendiumAuth.notarizedBasic
import io.bkbn.kompendium.models.meta.ResponseInfo
import io.bkbn.kompendium.models.oas.FormatSchema
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
import io.bkbn.kompendium.routes.openApi
import io.bkbn.kompendium.routes.redoc
import io.bkbn.kompendium.swagger.swaggerUI
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
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
  Kompendium.addCustomTypeSchema(DateTime::class, FormatSchema("date-time", "string"))

  embeddedServer(
    Netty,
    port = 8081,
    module = Application::mainModule
  ).start(wait = true)
}

var featuresInstalled = false

fun Application.configModule() {
  if (!featuresInstalled) {
    install(ContentNegotiation) {
      json()
    }
    install(Authentication) {
      notarizedBasic("basic") {
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
      notarizedException<Exception, ExceptionResponse>(
        info = ResponseInfo(
          HttpStatusCode.BadRequest,
          "Bad Things Happened",
          examples = mapOf("example" to ExceptionResponse("hey bad things happened sorry"))
        )
      ) {
        call.respond(HttpStatusCode.BadRequest, ExceptionResponse("Why you do dis?"))
      }
    }
    featuresInstalled = true
  }
}

fun Application.mainModule() {
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
  }
}
