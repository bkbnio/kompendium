package io.bkbn.kompendium.oas.component

import io.bkbn.kompendium.oas.schema.ComponentSchema
import io.bkbn.kompendium.oas.security.SecuritySchema
import kotlinx.serialization.Serializable

@Serializable
data class Components(
  val schemas: MutableMap<String, ComponentSchema> = mutableMapOf(),
  val securitySchemes: MutableMap<String, SecuritySchema> = mutableMapOf()
)
