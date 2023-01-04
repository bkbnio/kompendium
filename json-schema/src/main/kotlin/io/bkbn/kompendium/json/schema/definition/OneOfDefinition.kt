package io.bkbn.kompendium.json.schema.definition

import kotlinx.serialization.Serializable

@Serializable
data class OneOfDefinition(
  val oneOf: Set<JsonSchema>,
  override val deprecated: Boolean = false,
  override val description: String? = null
) : JsonSchema {
  constructor(vararg types: JsonSchema) : this(types.toSet())
}
