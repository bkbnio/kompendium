package org.leafygreens.kompendium.models.meta

data class RequestInfo(
  val description: String,
  val required: Boolean = true,
  val mediaTypes: List<String> = listOf("application/json")
)
