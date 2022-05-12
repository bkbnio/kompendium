package io.bkbn.kompendium.json.schema

import kotlinx.serialization.Serializable

//data class JsonSchema(
//  val `$schema`: String = "https://json-schema.org/draft/2020-12/schema",
//  val `$id`: String,
//  val title: String,
//  val type: String,
//  val format: String? = null,
//  val description: String? = null,
//  val properties: Map<String, JsonSchemaProperty>? = null,
//  val required: Set<String>? = null
//)

@Serializable
data class JsonSchema(
  val type: String,
  val format: String? = null,
  val description: String? = null,
  val properties: Map<String, JsonSchema>? = null,
  val required: Set<String>? = null
) {
  companion object {
    val INT = JsonSchema(
      type = "number",
      format = "int32"
    )

    val STRING = JsonSchema(
      type = "string"
    )

    val BOOLEAN = JsonSchema(
      type = "boolean"
    )
  }
}
