package io.bkbn.kompendium.oas.schema

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class SimpleSchema(
  override val type: String,
  override val default: @Contextual Any? = null,
  override val description: String? = null,
  override val nullable: Boolean? = null,
  // Constraints
  val minLength: Int? = null,
  val maxLength: Int? = null,
  val pattern: String? = null,
  val format: String? = null
) : TypedSchema
