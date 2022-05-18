package io.bkbn.kompendium.json.schema.definition

import kotlinx.serialization.Serializable

@Serializable
data class MapDefinition(
  val additionalProperties: JsonSchema,
  val type: Set<String> = setOf("object")
) : JsonSchema {
  companion object {
    fun nullable(additionalProperties: JsonSchema) = MapDefinition(
      additionalProperties = additionalProperties,
      type = setOf("null", "object")
    )
  }
}
