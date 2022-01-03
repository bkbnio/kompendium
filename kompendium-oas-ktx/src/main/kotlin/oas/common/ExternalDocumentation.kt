package oas.common

import java.net.URI

data class ExternalDocumentation(
  val url: URI,
  val description: String?
)
