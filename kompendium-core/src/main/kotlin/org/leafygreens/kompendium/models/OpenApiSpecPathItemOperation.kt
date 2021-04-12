package org.leafygreens.kompendium.models

data class OpenApiSpecPathItemOperation(
  val tags: Set<String> = emptySet(),
  val summary: String? = null,
  val description: String? = null,
  val externalDocs: OpenApiSpecExternalDocumentation? = null,
  val operationId: String? = null,
  val parameters: List<OpenApiSpecReferencable>? = null,
  val requestBody: OpenApiSpecReferencable? = null,
  // TODO How to enforce `default` requirement 🧐
  val responses: Map<String, OpenApiSpecReferencable>? = null,
  val callbacks: Map<String, OpenApiSpecReferencable>? = null,
  val deprecated: Boolean = false,
  // todo big yikes... also needs to reference objects in the security scheme 🤔
  val security: List<Map<String, List<String>>>? = null,
  val servers: List<OpenApiSpecServer>? = null,
  val `x-codegen-request-body-name`: String? = null
)
