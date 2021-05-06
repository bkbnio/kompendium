package org.leafygreens.kompendium.playground

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.webjars.Webjars
import org.leafygreens.kompendium.Notarized.notarizedDelete
import org.leafygreens.kompendium.Notarized.notarizedException
import org.leafygreens.kompendium.Notarized.notarizedGet
import org.leafygreens.kompendium.Notarized.notarizedPost
import org.leafygreens.kompendium.Notarized.notarizedPut
import org.leafygreens.kompendium.auth.KompendiumAuth.notarizedBasic
import org.leafygreens.kompendium.models.meta.ResponseInfo
import org.leafygreens.kompendium.playground.PlaygroundToC.testAuthenticatedSingleGetInfo
import org.leafygreens.kompendium.playground.PlaygroundToC.testGetWithExamples
import org.leafygreens.kompendium.playground.PlaygroundToC.testIdGetInfo
import org.leafygreens.kompendium.playground.PlaygroundToC.testPostWithExamples
import org.leafygreens.kompendium.playground.PlaygroundToC.testSingleDeleteInfo
import org.leafygreens.kompendium.playground.PlaygroundToC.testSingleGetInfo
import org.leafygreens.kompendium.playground.PlaygroundToC.testSingleGetInfoWithThrowable
import org.leafygreens.kompendium.playground.PlaygroundToC.testSinglePostInfo
import org.leafygreens.kompendium.playground.PlaygroundToC.testSinglePutInfo
import org.leafygreens.kompendium.routes.openApi
import org.leafygreens.kompendium.routes.redoc
import org.leafygreens.kompendium.swagger.swaggerUI
import org.leafygreens.kompendium.util.KompendiumHttpCodes

fun main() {
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
      jackson {
        enable(SerializationFeature.INDENT_OUTPUT)
        setSerializationInclusion(JsonInclude.Include.NON_NULL)
      }
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
          KompendiumHttpCodes.BAD_REQUEST,
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
