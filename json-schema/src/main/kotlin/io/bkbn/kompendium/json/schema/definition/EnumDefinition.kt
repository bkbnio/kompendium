package io.bkbn.kompendium.json.schema.definition

import kotlinx.serialization.Serializable

@Serializable
data class EnumDefinition(
  val enum: Set<String>,
  val type: String? = null
) : JsonSchema
