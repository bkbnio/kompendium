package org.leafygreens.kompendium.models

data class OpenApiSpecServer(
  val url: String,
  val description: String?,
  var variables: Map<String, OpenApiSpecServerVariable>?
)
