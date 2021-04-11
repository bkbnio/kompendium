package org.leafygreens.kompendium.models

import java.net.URI

data class OpenApiSpecOAuthFlow(
  val authorizationUrl: URI? = null,
  val tokenUrl: URI? = null,
  val refreshUrl: URI? = null,
  val scopes: Map<String, String>? = null
)
