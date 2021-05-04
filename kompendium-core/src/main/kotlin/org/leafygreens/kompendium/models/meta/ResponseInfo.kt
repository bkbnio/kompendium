package org.leafygreens.kompendium.models.meta

data class ResponseInfo<TResp>(
  val status: Int,
  val description: String,
  val mediaTypes: List<String> = listOf("application/json"),
  val examples: Map<String, TResp> = emptyMap()
)
