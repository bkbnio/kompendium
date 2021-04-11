package org.leafygreens.kompendium.models

import java.net.URI

data class OpenApiSpecOAuthFlow(
  val authorizationUrl: URI,
  val tokenUrl: URI,
  val refreshUrl: URI,
  val scopes: Map<String, String>
)
