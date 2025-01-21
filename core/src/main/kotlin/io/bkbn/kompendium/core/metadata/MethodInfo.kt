package io.bkbn.kompendium.core.metadata

import io.bkbn.kompendium.oas.common.ExternalDocumentation
import io.bkbn.kompendium.oas.payload.Parameter

sealed interface MethodInfo {
  val response: ResponseInfo
  val errors: List<ResponseInfo>
  val tags: Set<String>
  val summary: String
  val description: String

  val security: Map<String, List<String>>?
    get() = null
  val externalDocumentation: ExternalDocumentation?
    get() = null
  val operationId: String?
    get() = null
  val deprecated: Boolean
    get() = false
  val parameters: List<Parameter>
    get() = emptyList()

  abstract class Builder<T : MethodInfo> {
    internal var response: ResponseInfo? = null
    internal var summary: String? = null
    internal var description: String? = null
    internal var externalDocumentation: ExternalDocumentation? = null
    internal var operationId: String? = null
    internal var deprecated: Boolean = false
    internal var tags: Set<String> = emptySet()
    internal var parameters: List<Parameter> = emptyList()
    internal var errors: MutableList<ResponseInfo> = mutableListOf()
    internal var security: Map<String, List<String>>? = null

    fun response(init: ResponseInfo.Builder.() -> Unit) = apply {
      val builder = ResponseInfo.Builder()
      builder.init()
      this.response = builder.build()
    }

    fun canRespond(init: ResponseInfo.Builder.() -> Unit) = apply {
      val builder = ResponseInfo.Builder()
      builder.init()
      errors.add(builder.build())
    }

    fun canRespond(responses: List<ResponseInfo>) = apply {
      errors.addAll(responses)
    }

    fun summary(s: String) = apply { this.summary = s }

    fun description(s: String) = apply { this.description = s }

    fun externalDocumentation(docs: ExternalDocumentation) = apply { this.externalDocumentation = docs }

    fun operationId(id: String) = apply { this.operationId = id }

    fun isDeprecated() = apply { this.deprecated = true }

    fun tags(vararg tags: String) = apply { this.tags = tags.toSet() }

    fun parameters(vararg parameters: Parameter) = apply { this.parameters = parameters.toList() }

    fun security(security: Map<String, List<String>>) = apply { this.security = security }

    abstract fun build(): T
  }
}
