package io.bkbn.kompendium.models.oas

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.net.URI

@Serializable
data class OpenApiSpecServer(
  @Contextual
  val url: URI,
  val description: String? = null,
  var variables: Map<String, OpenApiSpecServerVariable>? = null
)
