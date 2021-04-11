package org.leafygreens.kompendium.models

data class OpenApiSpecTag(
  val name: String,
  val description: String?,
  val externalDocs: OpenApiSpecExternalDocumentation?
)
