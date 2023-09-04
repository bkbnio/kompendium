package io.bkbn.kompendium.playground

import io.bkbn.kompendium.core.metadata.GetInfo
import io.bkbn.kompendium.core.plugin.NotarizedApplication
import io.bkbn.kompendium.core.routes.redoc
import io.bkbn.kompendium.core.routes.swagger
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.bkbn.kompendium.oas.payload.Parameter
import io.bkbn.kompendium.oas.serialization.KompendiumSerializersModule
import io.bkbn.kompendium.playground.util.ExampleResponse
import io.bkbn.kompendium.playground.util.Util.baseSpec
import io.bkbn.kompendium.resources.NotarizedResources
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.resources.Resources
import io.ktor.server.resources.get
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

fun main() {
  embeddedServer(
    CIO,
    port = 8081,
    module = Application::mainModule
  ).start(wait = true)
}

private fun Application.mainModule() {
  install(Resources)
  install(ContentNegotiation) {
    json(Json {
      serializersModule = KompendiumSerializersModule.module
      encodeDefaults = true
      explicitNulls = false
    })
  }
  install(NotarizedApplication()) {
    spec = { baseSpec }
  }
  install(NotarizedResources()) {
    resources = mapOf(
      ListingResource::class to NotarizedResources.ResourceMetadata(
        parameters = listOf(
          Parameter(
            name = "name",
            `in` = Parameter.Location.path,
            schema = TypeDefinition.STRING
          ),
          Parameter(
            name = "page",
            `in` = Parameter.Location.path,
            schema = TypeDefinition.INT
          )
        ),
        get = GetInfo.builder {
          summary("Get user by id")
          description("A very neat endpoint!")
          response {
            responseCode(HttpStatusCode.OK)
            responseType<ExampleResponse>()
            description("Will return whether or not the user is real 😱")
          }
        }
      ),
    )
  }
  routing {
    swagger(pageTitle = "Simple API Docs")
    redoc(pageTitle = "Simple API Docs")
    get<ListingResource> { listing ->
      call.respondText("Listing ${listing.name}, page ${listing.page}")
    }
  }
}

@Serializable
@Resource("/list/{name}/page/{page}")
data class ListingResource(val name: String, val page: Int)
