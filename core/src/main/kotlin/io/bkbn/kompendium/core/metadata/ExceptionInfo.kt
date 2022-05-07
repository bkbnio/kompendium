package io.bkbn.kompendium.core.metadata

import io.ktor.http.HttpStatusCode
import kotlin.reflect.KType

data class ExceptionInfo<TResp : Any>(
  val responseType: KType,
  val status: HttpStatusCode,
  val description: String,
  val mediaTypes: List<String> = listOf("application/json"),
  val examples: Map<String, TResp> = emptyMap()
)
