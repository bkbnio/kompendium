package io.bkbn.kompendium.core.legacy.metadata

data class RequestInfo<TReq>(
  val description: String,
  val required: Boolean = true,
  val mediaTypes: List<String> = listOf("application/json"),
  val examples: Map<String, TReq> = emptyMap()
)
