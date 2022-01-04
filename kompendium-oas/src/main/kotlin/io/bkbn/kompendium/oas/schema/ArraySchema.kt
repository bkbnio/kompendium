package io.bkbn.kompendium.oas.schema

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class ArraySchema(
  val items: ComponentSchema,
  override val default: @Contextual Any? = null,
  override val description: String? = null,
  override val nullable: Boolean? = null,
  // constraints
  val minItems: Int? = null,
  val maxItems: Int? = null,
  val uniqueItems: Boolean? = null
) : TypedSchema {
  override val type: String = "array"
}
