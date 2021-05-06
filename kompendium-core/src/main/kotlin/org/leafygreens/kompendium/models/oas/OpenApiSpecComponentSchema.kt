package org.leafygreens.kompendium.models.oas

sealed class OpenApiSpecComponentSchema(open val default: Any? = null) {

  fun addDefault(default: Any?): OpenApiSpecComponentSchema = when (this) {
    is ReferencedSchema -> this.copy(default = default)
    is ObjectSchema -> this.copy(default = default)
    is DictionarySchema -> this.copy(default = default)
    is EnumSchema -> this.copy(default = default)
    is SimpleSchema -> this.copy(default = default)
    is FormatSchema -> this.copy(default = default)
    is ArraySchema -> this.copy(default = default)
  }

}

sealed class TypedSchema(open val type: String, override val default: Any? = null) : OpenApiSpecComponentSchema(default)

data class ReferencedSchema(val `$ref`: String, override val default: Any? = null) : OpenApiSpecComponentSchema(default)

data class ObjectSchema(
  val properties: Map<String, OpenApiSpecComponentSchema>,
  override val default: Any? = null
) : TypedSchema("object", default)

data class DictionarySchema(
  val additionalProperties: OpenApiSpecComponentSchema,
  override val default: Any? = null
) : TypedSchema("object", default)

data class EnumSchema(
  val `enum`: Set<String>, override val default: Any? = null
) : TypedSchema("string", default)

data class SimpleSchema(override val type: String, override val default: Any? = null) : TypedSchema(type, default)

data class FormatSchema(val format: String, override val type: String, override val default: Any? = null) :
  TypedSchema(type, default)

data class ArraySchema(val items: OpenApiSpecComponentSchema, override val default: Any? = null) :
  TypedSchema("array", default)

