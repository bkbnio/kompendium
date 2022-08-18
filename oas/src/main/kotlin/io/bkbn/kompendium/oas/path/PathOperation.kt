package io.bkbn.kompendium.oas.path

import io.bkbn.kompendium.oas.common.ExternalDocumentation
import io.bkbn.kompendium.oas.payload.Parameter
import io.bkbn.kompendium.oas.payload.Request
import io.bkbn.kompendium.oas.payload.Response
import io.bkbn.kompendium.oas.server.Server
import kotlinx.serialization.Serializable

/**
 * Describes a single API operation on a path.
 *
 * https://spec.openapis.org/oas/v3.1.0#operation-object
 *
 * @param tags A list of tags for API documentation control. Tags can be used for logical grouping of operations by resources or any other qualifier.
 * @param summary A short summary of what the operation does.
 * @param description A verbose explanation of the operation behavior.
 * @param externalDocs Additional external documentation for this operation.
 * @param operationId Unique string used to identify the operation. The id MUST be unique among
 * all operations described in the API. The operationId value is case-sensitive.
 * @param parameters A list of parameters that are applicable for this operation. If a parameter is already defined at
 * the Path Item, the new definition will override it but can never remove it. The list MUST NOT include duplicated parameters
 * @param requestBody The request body applicable for this operation.
 * @param responses The list of possible responses as they are returned from executing this operation.
 * @param callbacks A map of possible out-of band callbacks related to the parent operation.
 * @param deprecated Declares this operation to be deprecated.
 * @param security A declaration of which security mechanisms can be used for this operation.
 * @param servers An alternative server array to service this operation.
 */
@Serializable
data class PathOperation(
  var tags: Set<String> = emptySet(),
  var summary: String? = null,
  var description: String? = null,
  var externalDocs: ExternalDocumentation? = null,
  var operationId: String? = null,
  var parameters: List<Parameter>? = null,
  var requestBody: Request? = null,
  var responses: Map<Int, Response>? = null,
  var callbacks: Map<String, PathOperation>? = null,
  var deprecated: Boolean = false,
  var security: MutableList<Map<String, List<String>>>? = null,
  var servers: List<Server>? = null,
)
