package io.bkbn.kompendium.oas.server

import io.bkbn.kompendium.oas.serialization.UriSerializer
import kotlinx.serialization.Serializable
import java.net.URI

@Serializable
data class Server(
  @Serializable(with = UriSerializer::class)
  val url: URI,
  val description: String? = null,
  var variables: Map<String, ServerVariable>? = null
)
