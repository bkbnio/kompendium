package io.bkbn.kompendium.oas.old

data class OpenApiSpecServerVariable(
  val `enum`: Set<String>, // todo enforce not empty
  val default: String,
  val description: String?
)
