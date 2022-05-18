package io.bkbn.kompendium.json.schema.definition

import kotlinx.serialization.Serializable

@Serializable
data class ArrayDefinition(
  val items: JsonSchema,
  val type: Set<String> = setOf("array")
) : JsonSchema {
  companion object {
    fun nullable(items: JsonSchema) = ArrayDefinition(
      items = items,
      type = setOf("null", "array")
    )
  }
}
