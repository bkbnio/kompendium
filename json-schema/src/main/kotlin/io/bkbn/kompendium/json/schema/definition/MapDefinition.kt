package io.bkbn.kompendium.json.schema.definition

import kotlinx.serialization.Serializable

@Serializable
data class MapDefinition(
  val additionalProperties: JsonSchema,
  val maxProperties: Int? = null,
  val minProperties: Int? = null,
  override val deprecated: Boolean? = null,
  override val description: String? = null,
) : JsonSchema {
  val type: String = "object"
}
