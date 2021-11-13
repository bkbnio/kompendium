package io.bkbn.kompendium.oas.schema

data class DictionarySchema(
  val additionalProperties: ComponentSchema,
  override val default: Any? = null
) : TypedSchema {
  override val type: String = "object"
}
