package org.leafygreens.kompendium.models

data class OpenApiSpecRequest(
  val description: String?,
  val content: Map<String, OpenApiSpecMediaType>,
  val required: Boolean = false
)
