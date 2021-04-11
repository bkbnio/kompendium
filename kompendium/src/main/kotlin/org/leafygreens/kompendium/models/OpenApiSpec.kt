package org.leafygreens.kompendium.models

data class OpenApiSpec(
  val openapi: String = "3.0.3",
  val info: OpenApiSpecInfo? = null,
  // TODO Needs to default to server object with url of `/`
  val servers: List<OpenApiSpecServer>? = null,
  val paths: Map<String, OpenApiSpecPathItem>? = null,
  val components: OpenApiSpecComponents? = null,
  // todo needs to reference objects in the components -> security scheme 🤔
  val security: List<Map<String, List<String>>>? = null,
  val tags: List<OpenApiSpecTag>? = null,
  val externalDocs: OpenApiSpecExternalDocumentation? = null
)
