package org.leafygreens.kompendium.models

sealed class OpenApiSpecSchema

sealed class OpenApiSpecSchemaTyped(
  val type: String,
) : OpenApiSpecSchema()

data class OpenApiSpecSchemaArray<T: OpenApiSpecSchema >(
  val items: T
) : OpenApiSpecSchemaTyped("array")

data class OpenApiSpecSchemaString(
  val default: String,
  val `enum`: Set<String>? = null
) : OpenApiSpecSchemaTyped("string")

// TODO In Kt 1.5 Should be able to reference external sealed classes
data class OpenApiSpecSchemaRef(
  val `$ref`: String
) : OpenApiSpecSchema()
