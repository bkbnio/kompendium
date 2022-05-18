package io.bkbn.kompendium.core.metadata

import io.bkbn.kompendium.oas.common.ExternalDocumentation
import io.bkbn.kompendium.oas.payload.Parameter

class PatchInfo private constructor(
  val request: RequestInfo,
  override val response: ResponseInfo,
  override val tags: Set<String>,
  override val summary: String,
  override val description: String,
  override val externalDocumentation: ExternalDocumentation?,
  override val operationId: String?,
  override val deprecated: Boolean,
  override val parameters: List<Parameter>
): MethodInfo {

  companion object {
    fun builder(init: Builder.() -> Unit): PatchInfo {
      val builder = Builder()
      builder.init()
      return builder.build()
    }
  }

  class Builder : MethodInfo.Builder<PatchInfo>() {
    private var request: RequestInfo? = null

    fun request(init: RequestInfo.Builder.() -> Unit) = apply {
      val builder = RequestInfo.Builder()
      builder.init()
      this.request = builder.build()
    }

    override fun build() = PatchInfo(
      request = request!!,
      response = response!!,
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
