package io.bkbn.kompendium.oas.schema

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class EnumSchema(
  val `enum`: Set<String>,
  override val default: @Contextual Any? = null,
  override val description: String? = null,
  override val nullable: Boolean? = null
) : TypedSchema {
  override val type: String = "string"
}
