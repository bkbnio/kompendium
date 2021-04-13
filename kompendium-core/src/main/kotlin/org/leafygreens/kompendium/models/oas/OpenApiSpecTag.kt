package org.leafygreens.kompendium.models.oas

data class OpenApiSpecTag(
  val name: String,
  val description: String? = null,
  val externalDocs: OpenApiSpecExternalDocumentation? = null
)
