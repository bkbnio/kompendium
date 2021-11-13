package io.bkbn.kompendium.oas.server

import java.net.URI

data class Server(
  val url: URI,
  val description: String? = null,
  var variables: Map<String, ServerVariable>? = null
)
