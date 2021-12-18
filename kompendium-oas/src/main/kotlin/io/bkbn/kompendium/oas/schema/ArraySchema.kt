package io.bkbn.kompendium.oas.schema

data class ArraySchema(
  val items: ComponentSchema,
  override val default: Any? = null,
  override val description: String? = null
) : TypedSchema {
  override val type: String = "array"
}
