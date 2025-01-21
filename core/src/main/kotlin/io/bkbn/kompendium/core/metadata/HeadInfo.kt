package io.bkbn.kompendium.core.metadata

import io.bkbn.kompendium.oas.common.ExternalDocumentation
import io.bkbn.kompendium.oas.payload.Parameter

class HeadInfo private constructor(
  override val response: ResponseInfo,
  override val errors: MutableList<ResponseInfo>,
  override val tags: Set<String>,
  override val summary: String,
  override val description: String,
  override val externalDocumentation: ExternalDocumentation?,
  override val operationId: String?,
  override val deprecated: Boolean,
  override val parameters: List<Parameter>,
  override val security: Map<String, List<String>>?,
) : MethodInfo {

  companion object {
    fun builder(init: Builder.() -> Unit): HeadInfo {
      val builder = Builder()
      builder.init()
      return builder.build()
    }
  }

  class Builder : MethodInfo.Builder<HeadInfo>() {
    override fun build() = HeadInfo(
      response = response ?: error("Response info must be present"),
      errors = errors,
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
