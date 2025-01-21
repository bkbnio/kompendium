package io.bkbn.kompendium.playground

import io.bkbn.kompendium.core.metadata.GetInfo
import io.bkbn.kompendium.core.plugin.NotarizedApplication
import io.bkbn.kompendium.core.plugin.NotarizedRoute
import io.bkbn.kompendium.core.routes.redoc
import io.bkbn.kompendium.core.routes.swagger
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.bkbn.kompendium.oas.payload.Parameter
import io.bkbn.kompendium.oas.serialization.KompendiumSerializersModule
import io.bkbn.kompendium.playground.util.CustomTypeResponse
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
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlin.reflect.typeOf
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import java.util.UUID

fun main() {
  embeddedServer(
    CIO,
    port = 8081,
    module = Application::mainModule
  ).start(wait = true)
}

private fun Application.mainModule() {
  install(ContentNegotiation) {
    json(Json {
      serializersModule = KompendiumSerializersModule.module
      encodeDefaults = true
      explicitNulls = false
    })
  }
  install(NotarizedApplication()) {
    spec = { baseSpec }
    customTypes = mapOf(
      typeOf<Instant>() to TypeDefinition(type = "string", format = "date-time")
    )
  }
  routing {
    swagger(pageTitle = "Simple API Docs")
    redoc(pageTitle = "Simple API Docs")

    route("/{id}") {
      locationDocumentation()
      get {
        call.respond(
          HttpStatusCode.OK,
          CustomTypeResponse(
            thing = UUID.randomUUID().toString(),
            timestamp = Clock.System.now()
          )
        )
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
      summary("Important Thing")
      description("Do this often, it's important!")
      response {
        responseCode(HttpStatusCode.OK)
        responseType<CustomTypeResponse>()
        description("Showcases using a custom type!")
      }
    }
  }
}
