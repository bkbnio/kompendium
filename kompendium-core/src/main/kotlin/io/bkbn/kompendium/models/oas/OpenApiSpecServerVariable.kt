package io.bkbn.kompendium.models.oas

import kotlinx.serialization.Serializable

@Serializable
data class OpenApiSpecServerVariable(
  val `enum`: Set<String>, // todo enforce not empty
  val default: String,
  val description: String?
)
