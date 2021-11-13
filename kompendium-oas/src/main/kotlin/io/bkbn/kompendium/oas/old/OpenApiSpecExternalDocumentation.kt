package io.bkbn.kompendium.oas.old

import java.net.URI

data class OpenApiSpecExternalDocumentation(
  val url: URI,
  val description: String?
)
