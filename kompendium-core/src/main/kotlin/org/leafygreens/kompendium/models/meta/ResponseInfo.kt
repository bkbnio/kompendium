package org.leafygreens.kompendium.models.meta

data class ResponseInfo(
  val status: Int, // TODO How to handle error codes?
  val description: String,
  val mediaTypes: List<String> = listOf("application/json")
)
