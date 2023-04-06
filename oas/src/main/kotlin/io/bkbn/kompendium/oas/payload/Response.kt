package io.bkbn.kompendium.oas.payload

import kotlinx.serialization.Serializable

/**
 * Describes a single response from an API Operation
 *
 * https://spec.openapis.org/oas/v3.1.0#response-object
 *
 * @param description A description of the response.
 * @param headers Maps a header name to its definition.
 * @param content A map containing descriptions of potential response payloads.
 * The key is a media type or media type range and the value describes it.
 * @param links A map of operations links that can be followed from the response.
 */
@Serializable
data class Response(
  val description: String,
  val headers: Map<String, ResponseHeader>? = null,
  val content: Map<String, MediaType>? = null,
  val links: Map<String, Link>? = null
)
