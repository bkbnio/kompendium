package oas.path

import oas.common.ExternalDocumentation
import oas.payload.Parameter
import oas.payload.Payload
import oas.payload.Request
import oas.payload.Response
import oas.server.Server

data class PathOperation(
  var tags: Set<String> = emptySet(),
  var summary: String? = null,
  var description: String? = null,
  var externalDocs: ExternalDocumentation? = null,
  var operationId: String? = null,
  var parameters: List<Parameter>? = null,
  var requestBody: Request<*>? = null,
  // TODO How to enforce `default` requirement 🧐
  var responses: Map<Int, Response<*>>? = null,
  var callbacks: Map<String, Payload>? = null, // todo what is this?
  var deprecated: Boolean = false,
  var security: List<Map<String, List<String>>>? = null,
  var servers: List<Server>? = null,
  var `x-codegen-request-body-name`: String? = null
)
