package oas.schema

data class ObjectSchema(
  val properties: Map<String, ComponentSchema>,
  override val default: Any? = null,
  override val description: String? = null,
  override val nullable: Boolean? = null,
  // constraints
  val required: List<String>? = null
) : TypedSchema {
  override val type = "object"
}
