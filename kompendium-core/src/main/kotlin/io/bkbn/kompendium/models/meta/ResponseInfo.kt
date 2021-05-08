package io.bkbn.kompendium.models.meta

import io.ktor.http.HttpStatusCode

data class ResponseInfo<TResp>(
  val status: HttpStatusCode,
  val description: String,
  val mediaTypes: List<String> = listOf("application/json"),
  val examples: Map<String, TResp> = emptyMap()
)
