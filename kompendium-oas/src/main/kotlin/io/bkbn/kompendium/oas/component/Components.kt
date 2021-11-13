package io.bkbn.kompendium.oas.component

import io.bkbn.kompendium.oas.schema.SecuritySchema

data class Components(
  val securitySchemes: MutableMap<String, SecuritySchema> = mutableMapOf()
)
