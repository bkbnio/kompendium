package io.bkbn.kompendium.oas.old

sealed class OpenApiSpecComponentSchema(open val default: Any? = null) : OpenApiSpecReferencable {
  fun addDefault(default: Any?): OpenApiSpecComponentSchema = when (this) {
    is AnyOfReferencedSchema -> error("Cannot add default to anyOf reference")
    is ObjectSchema -> this.copy(default = default)
    is DictionarySchema -> this.copy(default = default)
    is EnumSchema -> this.copy(default = default)
    is SimpleSchema -> this.copy(default = default)
    is FormatSchema -> this.copy(default = default)
    is ArraySchema -> this.copy(default = default)
  }
}

sealed class TypedSchema(open val type: String, override val default: Any? = null) : OpenApiSpecComponentSchema(default)

data class AnyOfReferencedSchema(val anyOf: List<OpenApiSpecComponentSchema>) : OpenApiSpecComponentSchema()

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

