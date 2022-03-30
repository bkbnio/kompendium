package io.bkbn.kompendium.playground

import io.bkbn.kompendium.core.Kompendium
import io.bkbn.kompendium.core.Notarized.notarizedPost
import io.bkbn.kompendium.core.routes.redoc
import io.bkbn.kompendium.playground.util.Util
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
  embeddedServer(
    Netty,
    port = 8081,
    module = Application::mainModule
  ).start(wait = true)
}

private fun Application.mainModule() {
  install(ContentNegotiation) {
    gson {
      setPrettyPrinting()
    }
  }
  install(Kompendium) {
    spec = Util.baseSpec
  }
  routing {
    redoc()
    route("/create") {
      notarizedPost(BasicPlaygroundToC.simplePostRequest) {
        val request = call.receive<BasicModels.BasicRequest>()
        when (request.d) {
          true -> call.respond(HttpStatusCode.OK, BasicModels.BasicResponse(c = "So it is true!", null))
          false -> call.respond(HttpStatusCode.OK, BasicModels.BasicResponse(c = "Oh, I knew it!", null))
        }
      }
    }
  }
}
