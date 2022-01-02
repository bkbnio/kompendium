package io.bkbn.kompendium.oas.schema

data class FormattedSchema(
  val format: String,
  override val type: String,
  override val default: Any? = null,
  override val description: String? = null,
  override val nullable: Boolean? = null,
  // Constraints
  val minimum: Int? = null,
  val maximum: Int? = null,
  val exclusiveMinimum: Boolean? = null,
  val exclusiveMaximum: Boolean? = null,
  val multipleOf: Int? = null,
) : TypedSchema
