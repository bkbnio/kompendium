package io.bkbn.kompendium.playground

import io.bkbn.kompendium.Kompendium
import io.bkbn.kompendium.annotations.KompendiumParam
import io.bkbn.kompendium.annotations.ParamType
import io.bkbn.kompendium.locations.NotarizedLocation.notarizedGet
import io.bkbn.kompendium.models.meta.MethodInfo
import io.bkbn.kompendium.models.meta.ResponseInfo
import io.bkbn.kompendium.models.oas.FormatSchema
import io.bkbn.kompendium.playground.LocationsToC.testLocation
import io.bkbn.kompendium.routes.openApi
import io.bkbn.kompendium.routes.redoc
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.locations.Location
import io.ktor.locations.Locations
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.joda.time.DateTime

fun main() {
  Kompendium.addCustomTypeSchema(DateTime::class, FormatSchema("date-time", "string"))

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
    install(Locations)
    featuresInstalled = true
  }
}

private fun Application.mainModule() {
  configModule()
  routing {
    openApi(oas)
    redoc(oas)
    notarizedGet(testLocation) { tl ->
      call.respondText { tl.name }
    }
  }
}

private object LocationsToC {
  val testLocation = MethodInfo.GetInfo<TestLocations, ExampleResponse>(
    summary = "Example Parameters",
    description = "A test for setting parameter examples",
    responseInfo = ResponseInfo(
      status = HttpStatusCode.OK,
      description = "nice",
      examples = mapOf("test" to ExampleResponse(c = "spud"))
    ),
    canThrow = setOf(Exception::class)
  )
}

@Location("/test/{name}")
data class TestLocations(
  @KompendiumParam(ParamType.PATH)
  val name: String,
)
