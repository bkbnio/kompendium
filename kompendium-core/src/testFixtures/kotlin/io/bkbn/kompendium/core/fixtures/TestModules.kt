package io.bkbn.kompendium.core.fixtures

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import io.bkbn.kompendium.core.Kompendium
import io.bkbn.kompendium.core.routes.openApi
import io.bkbn.kompendium.core.routes.redoc
import io.bkbn.kompendium.oas.OpenApiSpec
import io.bkbn.kompendium.oas.info.Contact
import io.bkbn.kompendium.oas.info.Info
import io.bkbn.kompendium.oas.info.License
import io.bkbn.kompendium.oas.server.Server
import io.ktor.application.Application
import io.ktor.application.feature
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.jackson.jackson
import io.ktor.routing.routing
import java.net.URI

fun Application.docs() {
  routing {
    openApi(application.feature(Kompendium).config.spec)
    redoc(application.feature(Kompendium).config.spec)
  }
}

fun Application.jacksonConfigModule() {
  install(ContentNegotiation) {
    jackson(ContentType.Application.Json) {
      enable(SerializationFeature.INDENT_OUTPUT)
      setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }
  }
}

fun Application.kompendium() {
  install(Kompendium) {
    spec = OpenApiSpec(
      info = Info(
        title = "Test API",
        version = "1.33.7",
        description = "An amazing, fully-ish 😉 generated API spec",
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
}
