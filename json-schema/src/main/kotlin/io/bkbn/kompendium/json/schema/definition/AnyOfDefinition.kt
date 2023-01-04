package io.bkbn.kompendium.json.schema.definition

import kotlinx.serialization.Serializable

@Serializable
data class AnyOfDefinition(
  val anyOf: Set<JsonSchema>,
  override val deprecated: Boolean? = null,
  override val description: String? = null,
) : JsonSchema
