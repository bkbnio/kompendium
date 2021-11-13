package io.bkbn.kompendium.oas.schema

data class EnumSchema(
  val `enum`: Set<String>,
  override val default: Any? = null
) : TypedSchema {
  override val type: String = "string"
}
