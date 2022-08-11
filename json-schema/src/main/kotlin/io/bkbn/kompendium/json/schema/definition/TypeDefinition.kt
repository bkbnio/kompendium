package io.bkbn.kompendium.json.schema.definition

import kotlinx.serialization.Serializable

@Serializable
data class TypeDefinition(
  val type: String,
  val format: String? = null,
  val description: String? = null,
  val properties: Map<String, JsonSchema>? = null,
  val required: Set<String>? = null
) : JsonSchema {
  companion object {
    val INT = TypeDefinition(
      type = "number",
      format = "int32"
    )

    val LONG = TypeDefinition(
      type = "number",
      format = "int64"
    )

    val DOUBLE = TypeDefinition(
      type = "number",
      format = "double"
    )

    val FLOAT = TypeDefinition(
      type = "number",
      format = "float"
    )

    val STRING = TypeDefinition(
      type = "string"
    )

    val BOOLEAN = TypeDefinition(
      type = "boolean"
    )
  }
}
