package io.bkbn.kompendium.oas.schema

import io.bkbn.kompendium.oas.serialization.NumberSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class FormattedSchema(
  val format: String,
  override val type: String,
  override val default: @Contextual Any? = null,
  override val description: String? = null,
  override val nullable: Boolean? = null,
  // Constraints
  @Serializable(with = NumberSerializer::class)
  val minimum: Number? = null,
  @Serializable(with = NumberSerializer::class)
  val maximum: Number? = null,
  val exclusiveMinimum: Boolean? = null,
  val exclusiveMaximum: Boolean? = null,
  @Serializable(with = NumberSerializer::class)
  val multipleOf: Number? = null,
) : TypedSchema
