package io.bkbn.kompendium.playground

import io.bkbn.kompendium.core.Kompendium
import io.bkbn.kompendium.core.Notarized.notarizedGet
import io.bkbn.kompendium.core.metadata.ExceptionInfo
import io.bkbn.kompendium.core.metadata.ResponseInfo
import io.bkbn.kompendium.core.metadata.method.GetInfo
import io.bkbn.kompendium.core.routes.redoc
import io.bkbn.kompendium.oas.OpenApiSpec
import io.bkbn.kompendium.oas.info.Contact
import io.bkbn.kompendium.oas.info.Info
import io.bkbn.kompendium.oas.info.License
import io.bkbn.kompendium.oas.server.Server
import io.bkbn.kompendium.playground.ExceptionPlaygroundToC.simpleGetExample
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlin.reflect.typeOf
import kotlinx.serialization.Serializable
import java.net.URI
import java.time.LocalDateTime

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
    spec = ExceptionMetadata.spec
  }
  install(StatusPages) {
    exception<ExceptionModels.BadUserException> {
      call.respond(HttpStatusCode.BadRequest, ExceptionModels.ExceptionResponse("Bad user thing happened"))
    }
    exception<ExceptionModels.BadCoderException> {
      call.respond(HttpStatusCode.BadRequest, ExceptionModels.ExceptionResponse("Bad coder thing happened"))
    }
  }
  // Configures the routes for our API
  routing {
    // This adds ReDoc support at the `/docs` endpoint.
    // By default, it will point at the `/openapi.json` created by Kompendium
    redoc(pageTitle = "Simple Exception Examples")
    notarizedGet(simpleGetExample) {
      if (LocalDateTime.now().second % 2 == 0) {
        throw ExceptionModels.BadCoderException()
      } else {
        throw ExceptionModels.BadUserException()
      }
    }
  }
}

object ExceptionMetadata {
  val spec = OpenApiSpec(
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

// This is a table of contents to hold all the metadata for our various API endpoints
object ExceptionPlaygroundToC {
  private val simpleException = ExceptionInfo<ExceptionModels.ExceptionResponse>(
    responseType = typeOf<ExceptionModels.ExceptionResponse>(),
    status = HttpStatusCode.BadRequest,
    description = "Indicates that the user is a TOTAL LOSER hehehe"
  )

  private val badCoderSignal = ExceptionInfo<ExceptionModels.ExceptionResponse>(
    responseType = typeOf<ExceptionModels.ExceptionResponse>(),
    status = HttpStatusCode.InternalServerError,
    description = "Indicates that the engineer is a TOTAL NOOB mwahahaha"
  )

  val simpleGetExample = GetInfo<Unit, ExceptionModels.WontGetHere>(
    summary = "A route that throws an exception",
    description = "You will never see the real response MWAHAHAHA",
    responseInfo = ResponseInfo(
      HttpStatusCode.OK,
      description = "Not gonna happen pal"
    ),
    canThrow = setOf(simpleException, badCoderSignal)
  )
}

object ExceptionModels {
  @Serializable
  data class WontGetHere(val result: Int)

  @Serializable
  data class ExceptionResponse(val message: String)

  class BadUserException : Exception()

  class BadCoderException : Exception()
}
