package io.bkbn.kompendium.oas.schema

data class SimpleSchema(
  override val type: String,
  override val default: Any? = null,
  override val description: String? = null,
  // Constraints
  val minLength: Int? = null,
  val maxLength: Int? = null,
  val pattern: String? = null,
  val format: String? = null
) : TypedSchema
