package org.leafygreens.kompendium.models

data class OpenApiSpec(
  val openapi: String = "3.0.3",
  val info: OpenApiSpecInfo? = null,
  val servers: List<OpenApiSpecServer> = emptyList(), // TODO Needs to default to server object with url of `/`
  val paths: Map<String, OpenApiSpecPathItem> = emptyMap(),
  val components: OpenApiSpecComponents? = null,
  val security: Map<String, String> = emptyMap(), // todo needs to reference objects in the components -> security scheme 🤔
  val tags: List<OpenApiSpecTag> = emptyList(),
  val externalDocs: OpenApiSpecExternalDocumentation? = null
)
