package io.bkbn.kompendium.playground

import io.bkbn.kompendium.auth.NotarizedAuthentication
import io.bkbn.kompendium.core.metadata.GetInfo
import io.bkbn.kompendium.core.plugin.NotarizedApplication
import io.bkbn.kompendium.core.plugin.NotarizedRoute
import io.bkbn.kompendium.core.routes.redoc
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.bkbn.kompendium.oas.payload.Parameter
import io.bkbn.kompendium.oas.security.BasicAuth
import io.bkbn.kompendium.oas.serialization.KompendiumSerializersModule
import io.bkbn.kompendium.playground.util.ExampleResponse
import io.bkbn.kompendium.playground.util.Util
import io.bkbn.kompendium.playground.util.Util.baseSpec
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.basic
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json

/**
 * Application entrypoint.  Run this and head on over to `localhost:8081/docs`
 * to see a very simple yet beautifully documented API
 */
fun main() {
  embeddedServer(
    Netty,
    port = 8081,
    module = Application::mainModule
  ).start(wait = true)
}

private fun Application.mainModule() {
  // Installs Simple JSON Content Negotiation
  install(ContentNegotiation) {
    json(Json {
      serializersModule = KompendiumSerializersModule.module
      encodeDefaults = true
      explicitNulls = false
    })
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
  install(NotarizedApplication()) {
    spec = baseSpec
  }
  install(NotarizedAuthentication()) {
    securitySchemes = mapOf(
      "basic" to BasicAuth()
    )
  }
  routing {
    authenticate("basic") {

    }
    // This adds ReDoc support at the `/docs` endpoint.
    // By default, it will point at the `/openapi.json` created by Kompendium
    redoc(pageTitle = "Simple API Docs")

    // Route with a get handler
    route("/{id}") {
      idDocumentation()
      get {
        call.respond(HttpStatusCode.OK, ExampleResponse(true))
      }
    }
  }
}

// Documentation for our route
private fun Route.idDocumentation() {
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
      security = mapOf(
        "basic" to emptyList()
      )
      response {
        responseCode(HttpStatusCode.OK)
        responseType<ExampleResponse>()
        description("Will return whether or not the user is real 😱")
      }
    }
  }
}
