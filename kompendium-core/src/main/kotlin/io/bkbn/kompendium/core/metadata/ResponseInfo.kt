package io.bkbn.kompendium.core.metadata

import io.ktor.http.HttpStatusCode

data class ResponseInfo<TResp>(
  val status: HttpStatusCode,
  val description: String,
  val mediaTypes: List<String> = listOf("application/json"),
  val examples: Map<String, TResp> = emptyMap()
)
