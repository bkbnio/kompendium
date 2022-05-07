package io.bkbn.kompendium.oas.schema

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class DictionarySchema(
  val additionalProperties: ComponentSchema,
  override val default: @Contextual Any? = null,
  override val description: String? = null,
  override val nullable: Boolean? = null
) : TypedSchema {
  override val type: String = "object"
}
