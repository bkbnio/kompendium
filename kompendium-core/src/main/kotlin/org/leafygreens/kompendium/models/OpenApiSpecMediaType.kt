package org.leafygreens.kompendium.models

// TODO Oof -> https://swagger.io/specification/#media-type-object
data class OpenApiSpecMediaType(
  val schema: OpenApiSpecSchema, // TODO sheesh -> https://swagger.io/specification/#schema-object
  val example: String? = null, // TODO Enforce type? then serialize?
  val examples: Map<String, String>? = null, // needs to be mutually exclusive with example
  val encoding: Map<String, String>? = null // todo encoding object -> https://swagger.io/specification/#encoding-object
)
