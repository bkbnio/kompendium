package oas.payload

import kotlinx.serialization.Serializable

@Serializable
data class Request(
  val description: String?,
  val content: Map<String, MediaType>,
  val required: Boolean = false
) : Payload
