package io.bkbn.kompendium.models.oas

data class OpenApiSpecTag(
  val name: String,
  val description: String? = null,
  val externalDocs: OpenApiSpecExternalDocumentation? = null
)
