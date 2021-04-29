package org.leafygreens.kompendium.models.meta

data class ResponseInfo(
  val status: Int,
  val description: String,
  val mediaTypes: List<String> = listOf("application/json")
)
