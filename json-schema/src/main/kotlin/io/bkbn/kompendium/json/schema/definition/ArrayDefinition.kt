package io.bkbn.kompendium.json.schema.definition

import kotlinx.serialization.Serializable

@Serializable
data class ArrayDefinition(
  val items: JsonSchema,
  override val description: String? = null,
  override val deprecated: Boolean = false
) : JsonSchema {
  val type: String = "array"
}
