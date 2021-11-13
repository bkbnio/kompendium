package io.bkbn.kompendium.oas.schema

data class SimpleSchema(
  override val type: String,
  override val default: Any? = null
) : TypedSchema
