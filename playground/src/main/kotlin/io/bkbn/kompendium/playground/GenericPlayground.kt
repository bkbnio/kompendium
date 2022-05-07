package io.bkbn.kompendium.playground

import io.bkbn.kompendium.core.Kompendium
import io.bkbn.kompendium.core.Notarized.notarizedGet
import io.bkbn.kompendium.core.metadata.ResponseInfo
import io.bkbn.kompendium.core.metadata.method.GetInfo
import io.bkbn.kompendium.core.routes.redoc
import io.bkbn.kompendium.oas.serialization.KompendiumSerializersModule
import io.bkbn.kompendium.playground.GenericPlaygroundToC.simpleGenericGet
import io.bkbn.kompendium.playground.util.Util
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
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

// Application Module
private fun Application.mainModule() {
  // Installs Simple JSON Content Negotiation
  install(ContentNegotiation) {
    json(Json {
      serializersModule = KompendiumSerializersModule.module
      encodeDefaults = true
      explicitNulls = false
    })
  }
  // Installs the Kompendium Plugin and sets up baseline server metadata
  install(Kompendium) {
    spec = Util.baseSpec
  }
  routing {
    redoc(pageTitle = "Simple API Docs")
    notarizedGet(simpleGenericGet) {
      call.respond(
        HttpStatusCode.OK,
        GenericModels.Foosy(GenericModels.Barzo(5), listOf("hey", "now", "you're", "an", "all-start"))
      )
    }
  }
}


// This is a table of contents to hold all the metadata for our various API endpoints
object GenericPlaygroundToC {
  val simpleGenericGet = GetInfo<Unit, GenericModels.SimpleG<String>>(
    summary = "Lots 'o Generics",
    description = "Pretty funky huh",
    responseInfo = ResponseInfo(
      status = HttpStatusCode.OK,
      description = "Enjoy all this data, pal"
    )
  )
}

object GenericModels {
  @Serializable
  data class Foosy<T, K>(val test: T, val otherThing: List<K>)

  @Serializable
  data class Barzo<G>(val result: G)

  @Serializable
  data class SimpleG<G>(val result: G)
}
