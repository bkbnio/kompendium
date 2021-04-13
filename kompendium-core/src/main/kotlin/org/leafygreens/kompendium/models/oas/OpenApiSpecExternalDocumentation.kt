package org.leafygreens.kompendium.models.oas

import java.net.URI

data class OpenApiSpecExternalDocumentation(
  val url: URI,
  val description: String?
)
