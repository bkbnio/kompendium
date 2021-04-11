package org.leafygreens.kompendium.playground

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kompendium.Library

fun main() {
  embeddedServer(
    Netty,
    port = 8080,
    module = Application::mainModule
  ).start(wait = true)
}

fun Application.mainModule() {
  routing {
    route("/") {
      get {
        call.respondText(Library().someLibraryMethod())
      }
    }
  }
}
