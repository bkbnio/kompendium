package org.leafygreens.kompendium.models

data class OpenApiSpecPathItemOperation(
  val tags: Set<String> = emptySet(),
  val summary: String?,
  val description: String?,
  val externalDocs: OpenApiSpecExternalDocumentation?,
  val operationId: String?,
  val parameters: List<OpenApiSpecReferencable> = emptyList(),
  val requestBody: OpenApiSpecReferencable,
  val responses: Map<String, OpenApiSpecReferencable>, // TODO How to enforce `default` requirement
  val callbacks: Map<String, OpenApiSpecReferencable>,
  val deprecated: Boolean = false,
  val security: Map<String, String>, // todo needs to reference objects in the security scheme 🤔
  val servers: List<OpenApiSpecServer>
)
