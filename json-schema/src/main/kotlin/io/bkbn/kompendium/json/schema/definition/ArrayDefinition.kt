package io.bkbn.kompendium.json.schema.definition

import kotlinx.serialization.Serializable

@Serializable
data class ArrayDefinition(
  val items: JsonSchema,
  override val deprecated: Boolean? = null,
  override val description: String? = null,
) : JsonSchema {
  val type: String = "array"
}
