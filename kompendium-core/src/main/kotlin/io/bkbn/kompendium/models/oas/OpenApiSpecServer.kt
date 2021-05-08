package io.bkbn.kompendium.models.oas

import java.net.URI

data class OpenApiSpecServer(
  val url: URI,
  val description: String? = null,
  var variables: Map<String, OpenApiSpecServerVariable>? = null
)
