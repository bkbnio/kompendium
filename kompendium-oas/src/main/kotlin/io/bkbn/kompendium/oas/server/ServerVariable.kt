package io.bkbn.kompendium.oas.server

import kotlinx.serialization.Serializable

@Serializable
data class ServerVariable(
  val `enum`: Set<String>, // todo enforce not empty
  val default: String,
  val description: String?
)
