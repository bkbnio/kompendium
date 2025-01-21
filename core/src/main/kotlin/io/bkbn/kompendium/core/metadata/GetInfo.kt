package io.bkbn.kompendium.core.metadata

import io.bkbn.kompendium.oas.common.ExternalDocumentation
import io.bkbn.kompendium.oas.payload.Parameter

class GetInfo private constructor(
  override val response: ResponseInfo,
  override val errors: MutableList<ResponseInfo>,
  override val tags: Set<String>,
  override val summary: String,
  override val description: String,
  override val externalDocumentation: ExternalDocumentation?,
  override val operationId: String?,
  override val deprecated: Boolean,
  override val parameters: List<Parameter>,
  override val security: Map<String, List<String>>?
) : MethodInfo {

  companion object {
    fun builder(init: Builder.() -> Unit): GetInfo {
      val builder = Builder()
      builder.init()
      return builder.build()
    }
  }

  class Builder : MethodInfo.Builder<GetInfo>() {
    override fun build() = GetInfo(
      response = response ?: error("You must provide a response in order to notarize a GET"),
      errors = errors,
      tags = tags,
      summary = summary ?: error("You must provide a summary in order to notarize a GET"),
      description = description ?: error("You must provide a description in order to notarize a GET"),
      externalDocumentation = externalDocumentation,
      operationId = operationId,
      deprecated = deprecated,
      parameters = parameters,
      security = security
    )
  }
}
