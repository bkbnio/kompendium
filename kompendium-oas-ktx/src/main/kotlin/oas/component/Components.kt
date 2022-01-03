package oas.component

import kotlinx.serialization.Serializable
import oas.security.SecuritySchema

@Serializable
data class Components(
  val securitySchemes: MutableMap<String, SecuritySchema> = mutableMapOf()
)
