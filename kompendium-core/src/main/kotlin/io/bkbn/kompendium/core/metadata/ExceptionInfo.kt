package io.bkbn.kompendium.core.metadata

import io.ktor.http.HttpStatusCode
import kotlin.reflect.KClass

data class ExceptionInfo<TResp : Any>(
  val responseClass: KClass<TResp>,
  val status: HttpStatusCode,
  val description: String,
  val mediaTypes: List<String> = listOf("application/json"),
  val examples: Map<String, TResp> = emptyMap()
)
