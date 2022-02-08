package io.bkbn.kompendium.playground

import io.bkbn.kompendium.core.Kompendium
import io.bkbn.kompendium.core.Notarized.notarizedGet
import io.bkbn.kompendium.core.routes.swagger
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
import io.ktor.webjars.Webjars
import java.util.UUID

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
    json()
  }
  install(Webjars)
  // Installs the Kompendium Plugin and sets up baseline server metadata
  install(Kompendium) {
    spec = Util.baseSpec
  }
  // Configures the routes for our API
  routing {
    // This is all you need to do to add Swagger! Reachable at `/swagger-ui`
    swagger()
    // Kompendium infers the route path from the Ktor Route.  This will show up as the root path `/`
    notarizedGet(BasicPlaygroundToC.simpleGetExample) {
      call.respond(HttpStatusCode.OK, BasicModels.BasicResponse(c = UUID.randomUUID().toString()))
    }
  }
}
