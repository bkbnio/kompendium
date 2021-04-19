package org.leafygreens.kompendium.models.oas

typealias OpenApiSpecPathItemSecurity = List<Map<String, List<String>>>

data class OpenApiSpecPathItemOperation(
  var tags: Set<String> = emptySet(),
  var summary: String? = null,
  var description: String? = null,
  var externalDocs: OpenApiSpecExternalDocumentation? = null,
  var operationId: String? = null,
  var parameters: List<OpenApiSpecReferencable>? = null,
  var requestBody: OpenApiSpecReferencable? = null,
  // TODO How to enforce `default` requirement 🧐
  var responses: Map<Int, OpenApiSpecReferencable>? = null,
  var callbacks: Map<String, OpenApiSpecReferencable>? = null,
  var deprecated: Boolean = false,
  // todo big yikes... also needs to reference objects in the security scheme 🤔
  var security: OpenApiSpecPathItemSecurity? = null,
  var servers: List<OpenApiSpecServer>? = null,
  var `x-codegen-request-body-name`: String? = null
)
