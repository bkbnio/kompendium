package io.bkbn.kompendium.playground

import java.net.URI
import io.bkbn.kompendium.core.Kompendium
import io.bkbn.kompendium.oas.old.OpenApiSpecInfo
import io.bkbn.kompendium.oas.old.OpenApiSpecInfoContact
import io.bkbn.kompendium.oas.old.OpenApiSpecInfoLicense
import io.bkbn.kompendium.oas.old.OpenApiSpecServer

val oas = Kompendium.openApiSpec.copy(
  info = OpenApiSpecInfo(
    title = "Test API",
    version = "1.33.7",
    description = "An amazing, fully-ish 😉 generated API spec",
    termsOfService = URI("https://example.com"),
    contact = OpenApiSpecInfoContact(
      name = "Homer Simpson",
      email = "chunkylover53@aol.com",
      url = URI("https://gph.is/1NPUDiM")
    ),
    license = OpenApiSpecInfoLicense(
      name = "MIT",
      url = URI("https://github.com/bkbnio/kompendium/blob/main/LICENSE")
    )
  ),
  servers = mutableListOf(
    OpenApiSpecServer(
      url = URI("https://myawesomeapi.com"),
      description = "Production instance of my API"
    ),
    OpenApiSpecServer(
      url = URI("https://staging.myawesomeapi.com"),
      description = "Where the fun stuff happens"
    )
  )
)
