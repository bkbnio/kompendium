package io.bkbn.kompendium.oas.schema

data class DictionarySchema(
  val additionalProperties: ComponentSchema,
  override val default: Any? = null,
  override val description: String? = null
) : TypedSchema {
  override val type: String = "object"
}
