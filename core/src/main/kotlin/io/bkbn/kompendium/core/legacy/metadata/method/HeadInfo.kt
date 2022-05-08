package io.bkbn.kompendium.core.legacy.metadata.method

import io.bkbn.kompendium.core.legacy.metadata.ExceptionInfo
import io.bkbn.kompendium.core.legacy.metadata.ParameterExample
import io.bkbn.kompendium.core.legacy.metadata.ResponseInfo

data class HeadInfo<TParam>(
  override val responseInfo: ResponseInfo<Unit>,
  override val summary: String,
  override val description: String? = null,
  override val tags: Set<String> = emptySet(),
  override val deprecated: Boolean = false,
  override val securitySchemes: Set<String> = emptySet(),
  override val canThrow: Set<ExceptionInfo<*>> = emptySet(),
  override val parameterExamples: Set<ParameterExample> = emptySet(),
  override val operationId: String? = null
) : MethodInfo<TParam, Unit>
