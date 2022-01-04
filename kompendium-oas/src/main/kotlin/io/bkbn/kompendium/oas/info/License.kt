package io.bkbn.kompendium.oas.info

import io.bkbn.kompendium.oas.serialization.UriSerializer
import kotlinx.serialization.Serializable
import java.net.URI

@Serializable
data class License(
  var name: String,
  @Serializable(with = UriSerializer::class)
  var url: URI? = null
)
