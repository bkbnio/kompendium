package oas.schema

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
  val minimum: @Contextual Number? = null,
  val maximum: @Contextual Number? = null,
  val exclusiveMinimum: Boolean? = null,
  val exclusiveMaximum: Boolean? = null,
  val multipleOf: @Contextual Number? = null,
) : TypedSchema
