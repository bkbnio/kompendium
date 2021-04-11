package org.leafygreens.kompendium.models

data class OpenApiSpecServerVariable(
  val `enum`: Set<String>, // todo enforce not empty
  val default: String,
  val description: String?
)
