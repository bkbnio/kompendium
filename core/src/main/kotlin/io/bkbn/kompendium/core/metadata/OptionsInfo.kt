package io.bkbn.kompendium.core.metadata

import io.bkbn.kompendium.oas.common.ExternalDocumentation
import io.bkbn.kompendium.oas.payload.Parameter

class OptionsInfo private constructor(
  override val response: ResponseInfo,
  override val errors: MutableList<ResponseInfo>,
  override val tags: Set<String>,
  override val summary: String,
  override val description: String,
  override val externalDocumentation: ExternalDocumentation?,
  override val operationId: String?,
  override val deprecated: Boolean,
  override val parameters: List<Parameter>
): MethodInfo {

  companion object {
    fun builder(init: Builder.() -> Unit): OptionsInfo {
      val builder = Builder()
      builder.init()
      return builder.build()
    }
  }

  class Builder : MethodInfo.Builder<OptionsInfo>() {
    override fun build() = OptionsInfo(
      response = response!!,
      errors = errors,
      tags = tags,
      summary = summary!!,
      description = description!!,
      externalDocumentation = externalDocumentation,
      operationId = operationId,
      deprecated = deprecated,
      parameters = parameters
    )
  }

}
