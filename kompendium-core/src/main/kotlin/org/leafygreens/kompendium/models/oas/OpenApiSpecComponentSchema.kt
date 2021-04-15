package org.leafygreens.kompendium.models.oas

// TODO Enum for type?
sealed class OpenApiSpecComponentSchema

sealed class TypedSchema(open val type: String) : OpenApiSpecComponentSchema()

data class ReferencedSchema(val `$ref`: String) : OpenApiSpecComponentSchema()

data class ObjectSchema(
  val properties: Map<String, OpenApiSpecComponentSchema>
) : TypedSchema("object")

data class DictionarySchema(
  val additionalProperties: OpenApiSpecComponentSchema
) : TypedSchema("object")

data class EnumSchema(
  val `enum`: Set<String>
) : TypedSchema("string")

data class SimpleSchema(override val type: String) : TypedSchema(type)

data class FormatSchema(val format: String, override val type: String) : TypedSchema(type)

data class ArraySchema(val items: OpenApiSpecComponentSchema) : TypedSchema("array")
