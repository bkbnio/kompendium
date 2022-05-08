package io.bkbn.kompendium.core.legacy.metadata.method

import io.bkbn.kompendium.core.legacy.metadata.ExceptionInfo
import io.bkbn.kompendium.core.legacy.metadata.ParameterExample
import io.bkbn.kompendium.core.legacy.metadata.ResponseInfo

data class DeleteInfo<TParam, TResp>(
  override val responseInfo: ResponseInfo<TResp>,
  override val summary: String,
  override val description: String? = null,
  override val tags: Set<String> = emptySet(),
  override val deprecated: Boolean = false,
  override val securitySchemes: Set<String> = emptySet(),
  override val canThrow: Set<ExceptionInfo<*>> = emptySet(),
  override val parameterExamples: Set<ParameterExample> = emptySet(),
  override val operationId: String? = null
) : MethodInfo<TParam, TResp>
