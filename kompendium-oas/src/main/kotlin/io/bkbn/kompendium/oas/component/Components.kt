package io.bkbn.kompendium.oas.component

import io.bkbn.kompendium.oas.security.SecuritySchema
import kotlinx.serialization.Serializable

@Serializable
data class Components(
  val securitySchemes: MutableMap<String, SecuritySchema> = mutableMapOf()
)
