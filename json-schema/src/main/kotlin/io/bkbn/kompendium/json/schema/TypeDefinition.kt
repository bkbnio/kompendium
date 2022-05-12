package io.bkbn.kompendium.json.schema

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

    val STRING = TypeDefinition(
      type = "string"
    )

    val BOOLEAN = TypeDefinition(
      type = "boolean"
    )
  }
}
