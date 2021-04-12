package org.leafygreens.kompendium.models

// TODO I *think* the only thing I need here is the security https://swagger.io/specification/#components-object
data class OpenApiSpecComponents(
  val components: MutableMap<String, OpenApiSpecComponentSchema>,
  val securitySchemes: Map<String, OpenApiSpecSchema>
)
