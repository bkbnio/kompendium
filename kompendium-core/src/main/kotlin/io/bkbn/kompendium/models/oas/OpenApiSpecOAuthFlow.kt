package io.bkbn.kompendium.models.oas

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.net.URI

@Serializable
data class OpenApiSpecOAuthFlow(
  @Contextual
  val authorizationUrl: URI? = null,
  @Contextual
  val tokenUrl: URI? = null,
  @Contextual
  val refreshUrl: URI? = null,
  val scopes: Map<String, String>? = null
)
