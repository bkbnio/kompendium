package io.bkbn.kompendium.playground

import io.bkbn.kompendium.core.Kompendium
import io.bkbn.kompendium.core.Notarized.notarizedGet
import io.bkbn.kompendium.core.routes.redoc
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.locations.Locations
import io.ktor.response.respond
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
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
    install(Kompendium) {
      spec = oas
    }
    featuresInstalled = true
  }
}

private fun Application.mainModule() {
  configModule()
  routing {
    redoc()
    notarizedGet(PlaygroundToC.testGetWithExamples) {
      call.respond(HttpStatusCode.OK)
    }
  }
}
