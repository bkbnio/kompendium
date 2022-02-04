package io.bkbn.kompendium.oas.schema

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class ReferencedSchema(
  val `$ref`: String,
  override val default: @Contextual Any? = null,
  override val description: String? = null
) : ComponentSchema
