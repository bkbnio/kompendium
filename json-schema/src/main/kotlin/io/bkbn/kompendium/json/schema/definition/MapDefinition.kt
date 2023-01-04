package io.bkbn.kompendium.json.schema.definition

import kotlinx.serialization.Serializable

@Serializable
data class MapDefinition(
  val additionalProperties: JsonSchema,
  override val deprecated: Boolean = false,
  override val description: String? = null
) : JsonSchema {
  val type: String = "object"
}
