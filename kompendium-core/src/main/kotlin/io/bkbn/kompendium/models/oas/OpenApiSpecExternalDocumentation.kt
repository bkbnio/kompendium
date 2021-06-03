package io.bkbn.kompendium.models.oas

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.net.URI

@Serializable
data class OpenApiSpecExternalDocumentation(
  @Contextual
  val url: URI,
  val description: String?
)
