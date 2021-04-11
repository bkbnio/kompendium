package org.leafygreens.kompendium.models

sealed class OpenApiSpecParameterSchema(
  val type: String,
)

data class OpenApiSpecParameterSchemaArray<T: OpenApiSpecParameterSchema >(
  val items: T
) : OpenApiSpecParameterSchema("array")

data class OpenApiSpecParameterSchemaString(
  val default: String,
  val `enum`: Set<String>? = null
) : OpenApiSpecParameterSchema("string")
