package io.bkbn.kompendium.json.schema.definition

import kotlinx.serialization.Serializable

@Serializable
data class ArrayDefinition(
  val items: JsonSchema
) : JsonSchema {
  val type: String = "array"
}
