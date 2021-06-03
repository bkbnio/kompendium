package io.bkbn.kompendium.models.oas

import kotlinx.serialization.Serializable

@Serializable
data class OpenApiSpecOAuthFlows(
  val implicit: OpenApiSpecOAuthFlow?,
)
