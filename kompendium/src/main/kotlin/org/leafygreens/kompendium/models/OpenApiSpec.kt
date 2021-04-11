package org.leafygreens.kompendium.models

data class OpenApiSpec(
  val openapi: String = "3.0.3",
  val info: OpenApiSpecInfo? = null,
  // TODO Needs to default to server object with url of `/`
  val servers: List<OpenApiSpecServer> = emptyList(),
  val paths: Map<String, OpenApiSpecPathItem> = emptyMap(),
  val components: OpenApiSpecComponents? = null,
  // todo needs to reference objects in the components -> security scheme 🤔
  val security: Map<String, String> = emptyMap(),
  val tags: List<OpenApiSpecTag> = emptyList(),
  val externalDocs: OpenApiSpecExternalDocumentation? = null
)
