package oas.payload

data class Response(
  val description: String? = null,
  val headers: Map<String, Payload>? = null,
  val content: Map<String, MediaType>? = null,
  val links: Map<String, Payload>? = null
) : Payload
