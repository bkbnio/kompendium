package io.bkbn.kompendium.json.schema.definition

import kotlinx.serialization.Serializable

@Serializable
data class ArrayDefinition(
  val items: JsonSchema,
  override val deprecated: Boolean? = null,
  override val description: String? = null,

  // Constraints
  val maxItems: Int? = null,
  val minItems: Int? = null,
  val uniqueItems: Boolean? = null,
) : JsonSchema {
  val type: String = "array"
}
