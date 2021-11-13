package io.bkbn.kompendium.oas.old

data class OpenApiSpecTag(
  val name: String,
  val description: String? = null,
  val externalDocs: OpenApiSpecExternalDocumentation? = null
)
