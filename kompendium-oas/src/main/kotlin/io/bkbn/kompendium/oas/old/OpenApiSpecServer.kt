package io.bkbn.kompendium.oas.old

import java.net.URI

data class OpenApiSpecServer(
  val url: URI,
  val description: String? = null,
  var variables: Map<String, OpenApiSpecServerVariable>? = null
)
