package io.bkbn.kompendium.playground

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import io.bkbn.kompendium.core.metadata.GetInfo
import io.bkbn.kompendium.core.plugin.NotarizedApplication
import io.bkbn.kompendium.core.plugin.NotarizedRoute
import io.bkbn.kompendium.core.routes.redoc
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.bkbn.kompendium.oas.payload.Parameter
import io.bkbn.kompendium.playground.util.ExampleResponse
import io.bkbn.kompendium.playground.util.Util.baseSpec
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun main() {
  embeddedServer(
    Netty,
    port = 8081,
    module = Application::mainModule
  ).start(wait = true)
}

private fun Application.mainModule() {
  install(ContentNegotiation) {
    jackson {
      enable(SerializationFeature.INDENT_OUTPUT)
      setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }
  }
  install(NotarizedApplication()) {
    spec = baseSpec
  }
  routing {
    redoc(pageTitle = "Simple API Docs")

    route("/{id}") {
      locationDocumentation()
      get {
        call.respond(HttpStatusCode.OK, ExampleResponse(true))
      }
    }
  }
}

private fun Route.locationDocumentation() {
  install(NotarizedRoute()) {
    parameters = listOf(
      Parameter(
        name = "id",
        `in` = Parameter.Location.path,
        schema = TypeDefinition.STRING
      )
    )
    get = GetInfo.builder {
      summary("Get user by id")
      description("A very neat endpoint!")
      response {
        responseCode(HttpStatusCode.OK)
        responseType<ExampleResponse>()
        description("Will return whether or not the user is real 😱")
      }
    }
  }
}

