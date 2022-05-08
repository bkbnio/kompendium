package io.bkbn.kompendium.oas.payload

import kotlinx.serialization.Serializable

/**
 * Describes a single request body.
 *
 * https://spec.openapis.org/oas/v3.1.0#request-body-object
 *
 * @param description A brief description of the request body.
 * @param content The content of the request body. The key is a media type or media type range and the value describes it.
 * @param required Determines if the request body is required in the request.
 */
@Serializable
data class Request(
  val description: String?,
  val content: Map<String, MediaType>,
  val required: Boolean = false
)
