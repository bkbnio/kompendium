package io.bkbn.kompendium.core.legacy.metadata.method

import io.bkbn.kompendium.core.legacy.metadata.ExceptionInfo
import io.bkbn.kompendium.core.legacy.metadata.ParameterExample
import io.bkbn.kompendium.core.legacy.metadata.ResponseInfo

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
  val parameterExamples: Set<ParameterExample>
    get() = emptySet()
  val operationId: String?
    get() = null
}
