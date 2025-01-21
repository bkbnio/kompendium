package io.bkbn.kompendium.core.metadata

import io.bkbn.kompendium.oas.common.ExternalDocumentation
import io.bkbn.kompendium.oas.payload.Parameter

class PostInfo private constructor(
  override val request: RequestInfo?,
  override val errors: MutableList<ResponseInfo>,
  override val response: ResponseInfo,
  override val tags: Set<String>,
  override val summary: String,
  override val description: String,
  override val externalDocumentation: ExternalDocumentation?,
  override val operationId: String?,
  override val deprecated: Boolean,
  override val parameters: List<Parameter>,
  override val security: Map<String, List<String>>?,
) : MethodInfoWithRequest {

  companion object {
    fun builder(init: Builder.() -> Unit): PostInfo {
      val builder = Builder()
      builder.init()
      return builder.build()
    }
  }

  class Builder : MethodInfoWithRequest.Builder<PostInfo>() {
    override fun build() = PostInfo(
      request = request,
      errors = errors,
      response = response ?: error("response info must be present"),
      tags = tags,
      summary = summary ?: error("Summary must be present"),
      description = description ?: error("Description must be present"),
      externalDocumentation = externalDocumentation,
      operationId = operationId,
      deprecated = deprecated,
      parameters = parameters,
      security = security,
    )
  }
}
