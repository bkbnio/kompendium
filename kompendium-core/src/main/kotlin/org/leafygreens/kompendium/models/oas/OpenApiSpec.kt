package org.leafygreens.kompendium.models.oas

data class OpenApiSpec(
  val openapi: String = "3.0.3",
  val info: OpenApiSpecInfo,
  // TODO Needs to default to server object with url of `/`
  val servers: MutableList<OpenApiSpecServer> = mutableListOf(),
  val paths: MutableMap<String, OpenApiSpecPathItem> = mutableMapOf(),
  val components: OpenApiSpecComponents = OpenApiSpecComponents(),
  // todo needs to reference objects in the components -> security scheme 🤔
  val security: MutableList<Map<String, List<String>>> = mutableListOf(),
  val tags: MutableList<OpenApiSpecTag> = mutableListOf(),
  val externalDocs: OpenApiSpecExternalDocumentation? = null
)
