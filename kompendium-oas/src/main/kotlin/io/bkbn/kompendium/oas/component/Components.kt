package io.bkbn.kompendium.oas.component

import io.bkbn.kompendium.oas.security.SecuritySchema

data class Components(
  val securitySchemes: MutableMap<String, SecuritySchema> = mutableMapOf()
)
