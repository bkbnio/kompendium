package io.bkbn.kompendium.oas.common

data class Tag(
  val name: String,
  val description: String? = null,
  val externalDocs: ExternalDocumentation? = null
)
