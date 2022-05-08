package io.bkbn.kompendium.core

import io.bkbn.kompendium.core.metadata.GetInfo
import io.bkbn.kompendium.core.plugin.NotarizedApplication
import io.bkbn.kompendium.core.plugin.NotarizedRoute
import io.bkbn.kompendium.oas.OpenApiSpec
import io.bkbn.kompendium.oas.info.Contact
import io.bkbn.kompendium.oas.info.Info
import io.bkbn.kompendium.oas.info.License
import io.bkbn.kompendium.oas.payload.Parameter
import io.bkbn.kompendium.oas.schema.FormattedSchema
import io.bkbn.kompendium.oas.schema.SimpleSchema
import io.bkbn.kompendium.oas.server.Server
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import java.net.URI

fun main() {
  embeddedServer(CIO, port = 8080) {
    install(ContentNegotiation) {
      json(Json {
        explicitNulls = false
        encodeDefaults = true
      })
    }
    install(NotarizedApplication()) {
      spec = OpenApiSpec(
        info = Info(
          title = "Simple Demo API",
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
    routing {
      route("/") {
        rootDocumentation()
        get {
          call.respondText("Hello, world!")
        }
        route("/nesty") {
          nestyDocumentation()
          get {
            call.respondText("Hello, Nesty!")
          }
          route("{a}") {
            testPathParamDocs()
            get {
              call.respondText { "You sent: ${call.parameters["a"]}" }
            }
          }
        }
      }
    }
  }.start(wait = true)
}

fun Route.rootDocumentation() {
  install(NotarizedRoute()) {
    path = "/"
    tags = setOf("Neato")
    get = GetInfo.builder {
      responseType<String>()
      summary("Cool API Response")
      description("Cool Description")
      tags("Cool", "Stuff")
    }
  }
}

fun Route.nestyDocumentation() {
  install(NotarizedRoute()) {
    path = "/nesty"
    get = GetInfo.builder {
      responseType<String>()
      summary("Cool API Response")
      parameters(
        Parameter(
          name = "something",
          `in` = Parameter.Location.query,
          schema = SimpleSchema("string")
        )
      )
    }
  }
}

fun Route.testPathParamDocs() {
  install(NotarizedRoute()) {
    path = "/nesty/{a}"
    parameters = listOf(
      Parameter(
        name = "a",
        `in` = Parameter.Location.path,
        schema = FormattedSchema("int32", "integer")
      )
    )
    get = GetInfo.builder {
      responseType<String>()
      summary("Cool API Response")
    }
  }
}
