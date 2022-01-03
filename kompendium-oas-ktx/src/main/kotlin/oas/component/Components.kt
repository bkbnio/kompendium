package oas.component

import oas.security.SecuritySchema

data class Components(
  val securitySchemes: MutableMap<String, SecuritySchema> = mutableMapOf()
)
