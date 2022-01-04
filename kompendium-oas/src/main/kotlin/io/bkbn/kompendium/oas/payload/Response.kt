package io.bkbn.kompendium.oas.payload

import kotlinx.serialization.Serializable

@Serializable
data class Response(
  val description: String? = null,
  val headers: Map<String, Payload>? = null,
  val content: Map<String, MediaType>? = null,
  val links: Map<String, Payload>? = null
) : Payload
