package io.bkbn.kompendium.json.schema.definition

import kotlinx.serialization.Serializable

@Serializable
data class MapDefinition(
  val additionalProperties: JsonSchema
) : JsonSchema {
  val type: String = "object"
}
