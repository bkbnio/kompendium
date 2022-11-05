package io.bkbn.kompendium.json.schema.definition

import kotlinx.serialization.Serializable

@Serializable
data class EnumDefinition(
  val type: String,
  val enum: Set<String>
) : JsonSchema
