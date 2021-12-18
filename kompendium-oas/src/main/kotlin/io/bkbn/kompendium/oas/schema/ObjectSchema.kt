package io.bkbn.kompendium.oas.schema

data class ObjectSchema(
  val properties: Map<String, ComponentSchema>,
  override val default: Any? = null,
  override val description: String? = null
) : TypedSchema {
  override val type = "object"
}
