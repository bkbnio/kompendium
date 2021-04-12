package org.leafygreens.kompendium.playground

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.leafygreens.kompendium.Kompendium
import org.leafygreens.kompendium.Kompendium.notarizedGet
import org.leafygreens.kompendium.Kompendium.notarizedPost

fun main() {
  embeddedServer(
    Netty,
    port = 8080,
    module = Application::mainModule
  ).start(wait = true)
}

fun Application.mainModule() {
  install(ContentNegotiation) {
    jackson()
  }
  routing {
    route("/test") {
      route("/{id}") {
        notarizedGet {
          call.respondText("get by id")
        }
      }
      route("/single") {
        notarizedGet {
          call.respondText("get single")
        }
        notarizedPost {
          call.respondText("test post")
        }
      }
    }
    route("/openapi.json") {
      get {
        call.respond(Kompendium.spec)
      }
    }
  }
}
