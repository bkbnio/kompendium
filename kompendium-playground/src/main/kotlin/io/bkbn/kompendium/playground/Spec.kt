package io.bkbn.kompendium.playground

import java.net.URI
import io.bkbn.kompendium.core.Kompendium
import io.bkbn.kompendium.oas.info.Contact
import io.bkbn.kompendium.oas.info.Info
import io.bkbn.kompendium.oas.info.License
import io.bkbn.kompendium.oas.server.Server

val oas = Kompendium.openApiSpec.copy(
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
