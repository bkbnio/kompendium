package org.leafygreens.kompendium.models.meta

import kotlin.reflect.KClass

// TODO Seal and extend by method type? Yes ✅
data class MethodInfo<TParam, TReq, TResp>(
  val summary: String,
  val description: String? = null,
  val responseInfo: ResponseInfo<TResp>? = null,
  val requestInfo: RequestInfo<TReq>? = null,
  val tags: Set<String> = emptySet(),
  val deprecated: Boolean = false,
  val securitySchemes: Set<String> = emptySet(),
  val canThrow: Set<KClass<*>> = emptySet(),
  val parameterExamples: Map<String, TParam> = emptyMap(),
)
