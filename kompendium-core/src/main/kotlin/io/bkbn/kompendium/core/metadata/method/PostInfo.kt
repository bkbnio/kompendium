package io.bkbn.kompendium.core.metadata.method

import io.bkbn.kompendium.core.metadata.ExceptionInfo
import io.bkbn.kompendium.core.metadata.ParameterExample
import io.bkbn.kompendium.core.metadata.RequestInfo
import io.bkbn.kompendium.core.metadata.ResponseInfo

data class PostInfo<TParam, TReq, TResp>(
  val requestInfo: RequestInfo<TReq>?,
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
