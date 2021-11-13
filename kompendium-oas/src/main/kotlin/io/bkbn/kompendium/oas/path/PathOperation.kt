package io.bkbn.kompendium.oas.path

import io.bkbn.kompendium.oas.common.ExternalDocumentation
import io.bkbn.kompendium.oas.payload.Parameter
import io.bkbn.kompendium.oas.payload.Payload
import io.bkbn.kompendium.oas.payload.Request
import io.bkbn.kompendium.oas.payload.Response
import io.bkbn.kompendium.oas.server.Server

data class PathOperation(
  var tags: Set<String> = emptySet(),
  var summary: String? = null,
  var description: String? = null,
  var externalDocs: ExternalDocumentation? = null,
  var operationId: String? = null,
  var parameters: List<Parameter>? = null,
  var requestBody: Request<*>? = null,
  // TODO How to enforce `default` requirement 🧐
  var responses: Map<Int, Payload>? = null, // TODO Can be Response<*>?
  var callbacks: Map<String, Payload>? = null, // todo what is this?
  var deprecated: Boolean = false,
  var security: List<Map<String, List<String>>>? = null,
  var servers: List<Server>? = null,
  var `x-codegen-request-body-name`: String? = null
)
