package io.bkbn.kompendium.oas.payload

import io.bkbn.kompendium.oas.server.Server
import kotlinx.serialization.Serializable

/**
 * The Link object represents a possible design-time link for a response.
 *
 * https://spec.openapis.org/oas/v3.1.0#link-object
 *
 * @param operationRef A relative or absolute URI reference to an OAS operation.
 * @param operationId The name of an existing, resolvable OAS operation
 * @param parameters A map representing parameters to pass to an operation as specified with operationId or identified via operationRef.
 * @param requestBody Used as a request body when calling the target operation.
 * @param description A description of the link.
 * @param server A server object to be used by the target operation.
 */
@Serializable
data class Link(
  val operationRef: String? = null,
  val operationId: String? = null,
  val parameters: Map<String, String>? = null,
  val requestBody: Map<String, String>? = null,
  val description: String? = null,
  val server: Server? = null
)
