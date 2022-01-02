package io.bkbn.kompendium.oas.schema

data class FreeFormSchema(
  override val nullable: Boolean? = null,
  // constraints
  val minProperties: Int? = null,
  val maxProperties: Int? = null
) : TypedSchema {
  val additionalProperties: Boolean = true
  override val type: String = "object"
  override val default: Any? = null
}
