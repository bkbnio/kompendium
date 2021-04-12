package org.leafygreens.kompendium.playground

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.leafygreens.kompendium.annotations.KompendiumContact
import org.leafygreens.kompendium.annotations.KompendiumInfo
import org.leafygreens.kompendium.annotations.KompendiumLicense
import org.leafygreens.kompendium.annotations.KompendiumModule
import org.leafygreens.kompendium.annotations.KompendiumServers

@KompendiumInfo(
  title = "Test API",
  version = "0.0.1",
  description = "An API for testing"
)
@KompendiumContact(
  name = "Homer Simpson",
  url = "https://en.wikipedia.org/wiki/The_Simpsons",
  email = "chunkylover53@aol.com"
)
@KompendiumLicense(
  name = "DOH",
  url = "https://opensource.org/licenses/DOH"
)
@KompendiumServers(urls = [ "https://thesimpsonsquoteapi.glitch.me/quotes" ])
fun main() {
  embeddedServer(
    Netty,
    port = 8080,
    module = Application::mainModule
  ).start(wait = true)
}

@KompendiumModule
fun Application.mainModule() {
  routing {
    route("/") {
      get {
        call.respondText("hi")
      }
    }
  }
}
