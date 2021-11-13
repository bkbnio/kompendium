package io.bkbn.kompendium.oas.payload

data class Response<T>(
  val description: String? = null,
  val headers: Map<String, Payload>? = null,
  val content: Map<String, MediaType<T>>? = null,
  val links: Map<String, Payload>? = null
) : Payload
