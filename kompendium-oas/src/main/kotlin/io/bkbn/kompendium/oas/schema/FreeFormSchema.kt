package io.bkbn.kompendium.oas.schema

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class FreeFormSchema(
  override val nullable: Boolean? = null,
  // constraints
  val minProperties: Int? = null,
  val maxProperties: Int? = null,
  override val default: @Contextual Any? = null,
  override val description: String? = null,
) : TypedSchema {
  val additionalProperties: Boolean = true
  override val type: String = "object"
}
