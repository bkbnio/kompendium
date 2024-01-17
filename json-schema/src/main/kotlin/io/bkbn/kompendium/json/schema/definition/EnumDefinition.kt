package io.bkbn.kompendium.json.schema.definition

import kotlinx.serialization.Serializable

@Serializable
data class EnumDefinition(
  val enum: Set<String>,
  override val deprecated: Boolean? = null,
  override val description: String? = null,
) : JsonSchema
