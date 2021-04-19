package org.leafygreens.kompendium.models.oas

// TODO I *think* the only thing I need here is the security https://swagger.io/specification/#components-object
data class OpenApiSpecComponents(
  val schemas: MutableMap<String, OpenApiSpecComponentSchema> = mutableMapOf(),
  val securitySchemes: MutableMap<String, OpenApiSpecSchemaSecurity> = mutableMapOf()
)
