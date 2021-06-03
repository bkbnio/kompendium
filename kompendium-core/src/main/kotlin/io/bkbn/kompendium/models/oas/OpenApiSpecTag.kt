package io.bkbn.kompendium.models.oas

import kotlinx.serialization.Serializable

@Serializable
data class OpenApiSpecTag(
  val name: String,
  val description: String? = null,
  val externalDocs: OpenApiSpecExternalDocumentation? = null
)
