package io.bkbn.kompendium.oas.schema

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class ObjectSchema(
  val properties: Map<String, ComponentSchema>,
  override val default: @Contextual Any? = null,
  override val description: String? = null,
  override val nullable: Boolean? = null,
  // constraints
  val required: List<String>? = null
) : TypedSchema {
  override val type = "object"
}
