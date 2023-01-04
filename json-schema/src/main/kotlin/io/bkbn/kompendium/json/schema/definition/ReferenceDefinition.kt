package io.bkbn.kompendium.json.schema.definition

import kotlinx.serialization.Serializable

@Serializable
data class ReferenceDefinition(
  val `$ref`: String,
  override val description: String? = null,
  override val deprecated: Boolean = false
) : JsonSchema
