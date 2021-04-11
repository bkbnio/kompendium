package org.leafygreens.kompendium.models

data class OpenApiSpecTag(
  val name: String,
  val description: String? = null,
  val externalDocs: OpenApiSpecExternalDocumentation? = null
)
