package io.bkbn.kompendium.core.metadata.method

import io.bkbn.kompendium.core.metadata.ResponseInfo
import kotlin.reflect.KClass

sealed interface MethodInfo<TParam, TResp> {
  val summary: String
  val description: String?
    get() = null
  val tags: Set<String>
    get() = emptySet()
  val deprecated: Boolean
    get() = false
  val securitySchemes: Set<String>
    get() = emptySet()
  val canThrow: Set<KClass<*>>
    get() = emptySet()
  val responseInfo: ResponseInfo<TResp>?
    get() = null
  val parameterExamples: Map<String, TParam>
    get() = emptyMap()
  val operationId: String?
    get() = null
}
