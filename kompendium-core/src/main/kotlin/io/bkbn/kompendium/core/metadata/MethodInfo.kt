package io.bkbn.kompendium.core.metadata

import kotlin.reflect.KClass

sealed class MethodInfo<TParam, TResp>(
  open val summary: String,
  open val description: String? = null,
  open val tags: Set<String> = emptySet(),
  open val deprecated: Boolean = false,
  open val securitySchemes: Set<String> = emptySet(),
  open val canThrow: Set<KClass<*>> = emptySet(),
  open val responseInfo: ResponseInfo<TResp>? = null,
  open val parameterExamples: Map<String, TParam> = emptyMap(),
) {

  data class GetInfo<TParam, TResp>(
    override val responseInfo: ResponseInfo<TResp>? = null,
    override val summary: String,
    override val description: String? = null,
    override val tags: Set<String> = emptySet(),
    override val deprecated: Boolean = false,
    override val securitySchemes: Set<String> = emptySet(),
    override val canThrow: Set<KClass<*>> = emptySet(),
    override val parameterExamples: Map<String, TParam> = emptyMap()
  ) : MethodInfo<TParam, TResp>(
    summary = summary,
    description = description,
    tags = tags,
    deprecated = deprecated,
    securitySchemes = securitySchemes,
    canThrow = canThrow,
    responseInfo = responseInfo,
    parameterExamples = parameterExamples
  )

  data class PostInfo<TParam, TReq, TResp>(
    val requestInfo: RequestInfo<TReq>? = null,
    override val responseInfo: ResponseInfo<TResp>? = null,
    override val summary: String,
    override val description: String? = null,
    override val tags: Set<String> = emptySet(),
    override val deprecated: Boolean = false,
    override val securitySchemes: Set<String> = emptySet(),
    override val canThrow: Set<KClass<*>> = emptySet(),
    override val parameterExamples: Map<String, TParam> = emptyMap()
  ) : MethodInfo<TParam, TResp>(
    summary = summary,
    description = description,
    tags = tags,
    deprecated = deprecated,
    securitySchemes = securitySchemes,
    canThrow = canThrow,
    responseInfo = responseInfo,
    parameterExamples = parameterExamples
  )

  data class PutInfo<TParam, TReq, TResp>(
    val requestInfo: RequestInfo<TReq>? = null,
    override val responseInfo: ResponseInfo<TResp>? = null,
    override val summary: String,
    override val description: String? = null,
    override val tags: Set<String> = emptySet(),
    override val deprecated: Boolean = false,
    override val securitySchemes: Set<String> = emptySet(),
    override val canThrow: Set<KClass<*>> = emptySet(),
    override val parameterExamples: Map<String, TParam> = emptyMap()
  ) : MethodInfo<TParam, TResp>(
    summary = summary,
    description = description,
    tags = tags,
    deprecated = deprecated,
    securitySchemes = securitySchemes,
    canThrow = canThrow,
    parameterExamples = parameterExamples
  )

  data class DeleteInfo<TParam, TResp>(
    override val responseInfo: ResponseInfo<TResp>? = null,
    override val summary: String,
    override val description: String? = null,
    override val tags: Set<String> = emptySet(),
    override val deprecated: Boolean = false,
    override val securitySchemes: Set<String> = emptySet(),
    override val canThrow: Set<KClass<*>> = emptySet(),
    override val parameterExamples: Map<String, TParam> = emptyMap()
  ) : MethodInfo<TParam, TResp>(
    summary = summary,
    description = description,
    tags = tags,
    deprecated = deprecated,
    securitySchemes = securitySchemes,
    canThrow = canThrow,
    parameterExamples = parameterExamples
  )
}
