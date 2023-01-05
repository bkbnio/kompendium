package io.bkbn.kompendium.json.schema.definition

import io.bkbn.kompendium.json.schema.util.Serializers
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class TypeDefinition(
  val type: String,
  val format: String? = null,
  val properties: Map<String, JsonSchema>? = null,
  val required: Set<String>? = null,
  @Contextual val default: Any? = null,
  override val deprecated: Boolean? = null,
  override val description: String? = null,
  // Constraints

  // Number
  @Serializable(with = Serializers.Number::class)
  val multipleOf: Number? = null,
  @Serializable(with = Serializers.Number::class)
  val maximum: Number? = null,
  @Serializable(with = Serializers.Number::class)
  val exclusiveMaximum: Number? = null,
  @Serializable(with = Serializers.Number::class)
  val minimum: Number? = null,
  @Serializable(with = Serializers.Number::class)
  val exclusiveMinimum: Number? = null,

  // String
  val maxLength: Int? = null,
  val minLength: Int? = null,
  val pattern: String? = null,
  val contentEncoding: String? = null,
  val contentMediaType: String? = null,

  // Object
  val maxProperties: Int? = null,
  val minProperties: Int? = null,
) : JsonSchema {

  fun withDefault(default: Any): TypeDefinition = this.copy(default = default)

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

    val UUID = TypeDefinition(
      type = "string",
      format = "uuid"
    )

    val BOOLEAN = TypeDefinition(
      type = "boolean"
    )
  }
}
