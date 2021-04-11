package org.leafygreens.kompendium.models

// TODO I *think* the only thing I need here is the security https://swagger.io/specification/#components-object
data class OpenApiSpecComponents(
  val securitySchemes: Map<String, OpenApiSpecReferencable>
)
