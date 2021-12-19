package io.bkbn.kompendium.playground

import io.bkbn.kompendium.core.Kompendium
import io.bkbn.kompendium.core.Notarized.notarizedGet
import io.bkbn.kompendium.core.metadata.ResponseInfo
import io.bkbn.kompendium.core.metadata.method.GetInfo
import io.bkbn.kompendium.core.routes.redoc
import io.bkbn.kompendium.oas.OpenApiSpec
import io.bkbn.kompendium.oas.info.Contact
import io.bkbn.kompendium.oas.info.Info
import io.bkbn.kompendium.oas.info.License
import io.bkbn.kompendium.oas.server.Server
import io.bkbn.kompendium.playground.PolymorphicPlaygroundToC.polymorphicExample
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
import java.net.URI

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
  // Installs Simple JSON Content Negotiation
  install(ContentNegotiation) {
    json()
  }
  // Installs the Kompendium Plugin and sets up baseline server metadata
  install(Kompendium) {
    spec = PolymorphicMetadata.spec
  }
  // Configures the routes for our API
  routing {
    redoc(pageTitle = "Polymorphic API Examples")
    notarizedGet(polymorphicExample) {
      call.respond(HttpStatusCode.OK, PolymorphicModels.OneJamma(1337))
    }
  }
}

// This is a table of contents to hold all the metadata for our various API endpoints
object PolymorphicPlaygroundToC {
  val polymorphicExample = GetInfo<Unit, PolymorphicModels.SlammaJamma>(
    summary = "C'mon and Slam",
    description = "And welcome to the jam",
    responseInfo = ResponseInfo(
      status = HttpStatusCode.OK,
      description = "You have successfully slammed and/or jammed",
      examples = mapOf(
        "one" to PolymorphicModels.OneJamma(42),
        "two" to PolymorphicModels.AnothaJamma(4.2)
      )
    )
  )
}

object PolymorphicMetadata {
  val spec = OpenApiSpec(
    info = Info(
      title = "Simple Demo API with Polymorphic Models",
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

object PolymorphicModels {
  sealed interface SlammaJamma

  @Serializable
  data class OneJamma(val a: Int) : SlammaJamma

  @Serializable
  data class AnothaJamma(val b: Double) : SlammaJamma
}
