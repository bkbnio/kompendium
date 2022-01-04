package io.bkbn.kompendium.oas.common

import io.bkbn.kompendium.oas.serialization.UriSerializer
import kotlinx.serialization.Serializable
import java.net.URI

@Serializable
data class ExternalDocumentation(
  @Serializable(with = UriSerializer::class)
  val url: URI,
  val description: String?
)
