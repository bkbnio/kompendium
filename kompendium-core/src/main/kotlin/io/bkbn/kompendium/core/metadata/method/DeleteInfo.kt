package io.bkbn.kompendium.core.metadata.method

import io.bkbn.kompendium.core.metadata.ResponseInfo
import kotlin.reflect.KClass

data class DeleteInfo<TParam, TResp>(
  override val responseInfo: ResponseInfo<TResp>? = null,
  override val summary: String,
  override val description: String? = null,
  override val tags: Set<String> = emptySet(),
  override val deprecated: Boolean = false,
  override val securitySchemes: Set<String> = emptySet(),
  override val canThrow: Set<KClass<*>> = emptySet(),
  override val parameterExamples: Map<String, TParam> = emptyMap(),
  override val operationId: String? = null
) : MethodInfo<TParam, TResp>
