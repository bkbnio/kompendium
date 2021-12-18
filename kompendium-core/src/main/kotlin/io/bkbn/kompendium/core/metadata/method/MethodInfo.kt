package io.bkbn.kompendium.core.metadata.method

import io.bkbn.kompendium.core.metadata.ExceptionInfo
import io.bkbn.kompendium.core.metadata.ResponseInfo

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
  val canThrow: Set<ExceptionInfo<*>>
    get() = emptySet()
  val responseInfo: ResponseInfo<TResp>
  // TODO Is this even used anywhere?
  val parameterExamples: Map<String, TParam>
    get() = emptyMap()
  val operationId: String?
    get() = null
}
