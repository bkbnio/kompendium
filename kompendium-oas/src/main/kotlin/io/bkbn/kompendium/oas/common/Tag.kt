package io.bkbn.kompendium.oas.common

import kotlinx.serialization.Serializable

@Serializable
data class Tag(
  val name: String,
  val description: String? = null,
  val externalDocs: ExternalDocumentation? = null
)
