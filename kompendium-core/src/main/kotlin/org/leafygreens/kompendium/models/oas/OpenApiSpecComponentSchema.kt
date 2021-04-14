package org.leafygreens.kompendium.models.oas

// TODO Enum for type?
sealed class OpenApiSpecComponentSchema(open val type: String)

data class ObjectSchema(
  val properties: Map<String, OpenApiSpecComponentSchema>
) : OpenApiSpecComponentSchema("object")

data class DictionarySchema(
  val additionalProperties: OpenApiSpecComponentSchema
) : OpenApiSpecComponentSchema("object")

data class EnumSchema(
  val `enum`: Set<String>
) : OpenApiSpecComponentSchema("string")

data class SimpleSchema(override val type: String) : OpenApiSpecComponentSchema(type)

data class FormatSchema(val format: String, override val type: String) : OpenApiSpecComponentSchema(type)

data class ArraySchema(val items: OpenApiSpecComponentSchema) : OpenApiSpecComponentSchema("array")
