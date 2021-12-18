package io.bkbn.kompendium.oas.schema

data class FormattedSchema(
  val format: String,
  override val type: String,
  override val default: Any? = null,
  override val description: String? = null
) : TypedSchema
