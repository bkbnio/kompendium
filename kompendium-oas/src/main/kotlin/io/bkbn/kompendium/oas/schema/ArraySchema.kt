package io.bkbn.kompendium.oas.schema

data class ArraySchema(
  val items: ComponentSchema,
  override val default: Any? = null
) : TypedSchema {
  override val type: String = "array"
}
