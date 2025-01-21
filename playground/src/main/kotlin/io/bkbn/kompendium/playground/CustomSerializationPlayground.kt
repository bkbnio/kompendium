package io.bkbn.kompendium.playground

import io.bkbn.kompendium.core.metadata.GetInfo
import io.bkbn.kompendium.core.plugin.NotarizedApplication
import io.bkbn.kompendium.core.plugin.NotarizedRoute
import io.bkbn.kompendium.core.routes.redoc
import io.bkbn.kompendium.core.routes.swagger
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.bkbn.kompendium.oas.component.Components
import io.bkbn.kompendium.oas.payload.Parameter
import io.bkbn.kompendium.oas.security.BasicAuth
import io.bkbn.kompendium.oas.serialization.KompendiumSerializersModule
import io.bkbn.kompendium.playground.util.ExampleResponse
import io.bkbn.kompendium.playground.util.Util.baseSpec
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun main() {
  embeddedServer(
    CIO,
    port = 8081,
    module = Application::mainModule
  ).start(wait = true)
}

private val CustomJsonEncoder = Json {
  serializersModule = KompendiumSerializersModule.module
  encodeDefaults = true
  explicitNulls = false
}

private fun Application.mainModule() {
  install(ContentNegotiation) {
    json(Json {
      serializersModule = KompendiumSerializersModule.module
      encodeDefaults = true
      explicitNulls = true
    })
  }
  install(NotarizedApplication()) {
    spec = {
      baseSpec.copy(
        components = Components(
          securitySchemes = mutableMapOf(
            "basic" to BasicAuth()
          )
        )
      )
    }
    specRoute = { spec, routing ->
      routing {
        route("/openapi.json") {
          get {
            call.response.headers.append("Content-Type", "application/json")
            call.respondText { CustomJsonEncoder.encodeToString(spec) }
          }
        }
      }
    }
  }
  routing {
    swagger(pageTitle = "Simple API Docs")
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
