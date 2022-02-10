package io.bkbn.kompendium.playground

import io.bkbn.kompendium.annotations.Param
import io.bkbn.kompendium.annotations.ParamType
import io.bkbn.kompendium.core.Kompendium
import io.bkbn.kompendium.core.metadata.ResponseInfo
import io.bkbn.kompendium.core.metadata.method.GetInfo
import io.bkbn.kompendium.core.routes.redoc
import io.bkbn.kompendium.locations.NotarizedLocation.notarizedGet
import io.bkbn.kompendium.oas.serialization.KompendiumSerializersModule
import io.bkbn.kompendium.playground.LocationsToC.ohBoiUCrazy
import io.bkbn.kompendium.playground.LocationsToC.testLocation
import io.bkbn.kompendium.playground.LocationsToC.testNestLocation
import io.bkbn.kompendium.playground.util.Util
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.locations.Location
import io.ktor.locations.Locations
import io.ktor.response.respondText
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.serialization.Serializable
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
  install(ContentNegotiation) {
    json(Json {
      serializersModule = KompendiumSerializersModule.module
      encodeDefaults = true
      explicitNulls = false
    })
  }
  install(Kompendium) {
    spec = Util.baseSpec
  }
  install(Locations)
  routing {
    redoc()
    /**
     * Notice the difference here between this and the standard notarizedGet!  tl contains your input parameters
     */
    notarizedGet(testLocation) { tl ->
      call.respondText { tl.name }
    }
    notarizedGet(testNestLocation) { tnl ->
      call.respondText { tnl.idk.toString() }
    }
    notarizedGet(ohBoiUCrazy) { obuc ->
      call.respondText { obuc.parent.parent.name }
    }
  }
}

private object LocationsToC {
  val testLocation = GetInfo<TestLocations, LocationModels.ExampleResponse>(
    summary = "Shallow",
    description = "Ez Pz Lemon Squeezy",
    responseInfo = ResponseInfo(
      status = HttpStatusCode.OK,
      description = "Great!"
    )
  )
  val testNestLocation = GetInfo<TestLocations.NestedTestLocations, LocationModels.ExampleResponse>(
    summary = "Nested",
    description = "Gettin' scary",
    responseInfo = ResponseInfo(
      status = HttpStatusCode.Continue,
      description = "Hmmm"
    )
  )
  val ohBoiUCrazy = GetInfo<TestLocations.NestedTestLocations.OhBoiUCrazy, LocationModels.ExampleResponse>(
    summary = "Example Deeply Nested",
    description = "We deep now",
    responseInfo = ResponseInfo(
      status = HttpStatusCode.OK,
      description = "nice",
      examples = mapOf("test" to LocationModels.ExampleResponse(c = "spud"))
    ),
  )
}

// For more info make sure to read through the Ktor location docs
// Additionally, make sure to note that even though we define the locations here, we still must annotate fields
// with KompendiumParam!!!
@Location("test/{name}")
data class TestLocations(
  @Param(ParamType.PATH)
  val name: String,
) {
  @Location("/spaghetti")
  data class NestedTestLocations(
    @Param(ParamType.QUERY)
    val idk: Int,
    val parent: TestLocations
  ) {
    @Location("/hehe/{madness}")
    data class OhBoiUCrazy(
      @Param(ParamType.PATH)
      val madness: Boolean,
      val parent: NestedTestLocations
    )
  }
}

object LocationModels {
  @Serializable
  data class ExampleResponse(val c: String)
}
