package io.bkbn.kompendium.models.oas

import kotlinx.serialization.Serializable

// TODO I *think* the only thing I need here is the security https://swagger.io/specification/#components-object
@Serializable
data class OpenApiSpecComponents(
  val schemas: MutableMap<String, OpenApiSpecComponentSchema> = mutableMapOf(),
  val securitySchemes: MutableMap<String, OpenApiSpecSchemaSecurity> = mutableMapOf()
)
