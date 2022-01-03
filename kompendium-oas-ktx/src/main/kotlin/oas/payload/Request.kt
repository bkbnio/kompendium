package oas.payload

data class Request<T>(
  val description: String?,
  val content: Map<String, MediaType<T>>,
  val required: Boolean = false
) : Payload
