package io.bkbn.kompendium.playground

import io.bkbn.kompendium.core.Kompendium
import io.bkbn.kompendium.core.Notarized.notarizedGet
import io.bkbn.kompendium.core.metadata.method.GetInfo
import io.bkbn.kompendium.core.routes.redoc
import io.bkbn.kompendium.oas.OpenApiSpec
import io.bkbn.kompendium.oas.info.Contact
import io.bkbn.kompendium.oas.info.Info
import io.bkbn.kompendium.oas.info.License
import io.bkbn.kompendium.oas.server.Server
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
import java.net.URI

// Application Entrypoint
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
  // Installs the Kompendium Plugin and sets up baseline server metadata
  install(Kompendium) {
    spec = OpenApiSpec(
      info = Info(
        title = "Simple Demo API with notarized exceptions",
        version = "1.33.7",
        description = "Wow isn't this cool?",
        termsOfService = URI("https://example.com"),
        contact = Contact(
          name = "Homer Simpson",
          email = "chunkylover53@aol.com",
          url = URI("https://gph.is/1NPUDiM")
        ),
        license = License(
          name = "MIT",
          url = URI("https://github.com/bkbnio/kompendium/blob/main/LICENSE")
        )
      ),
      servers = mutableListOf(
        Server(
          url = URI("https://myawesomeapi.com"),
          description = "Production instance of my API"
        ),
        Server(
          url = URI("https://staging.myawesomeapi.com"),
          description = "Where the fun stuff happens"
        )
      )
    )
  }
  // Configures the routes for our API
  routing {
    // This adds ReDoc support at the `/docs` endpoint.
    // By default, it will point at the `/openapi.json` created by Kompendium
    redoc()
    notarizedGet(PlaygroundToC.testGetWithExamples) {
      call.respond(HttpStatusCode.OK)
    }
  }
}
