package org.leafygreens.kompendium.models.meta

import kotlin.reflect.KClass

// TODO Seal and extend by method type?
data class MethodInfo(
  val summary: String,
  val description: String? = null,
  val responseInfo: ResponseInfo? = null,
  val requestInfo: RequestInfo? = null,
  val tags: Set<String> = emptySet(),
  val deprecated: Boolean = false,
  val securitySchemes: Set<String> = emptySet(),
  val canThrow: Set<KClass<*>> = emptySet()
)
