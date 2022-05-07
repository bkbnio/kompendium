package io.bkbn.kompendium.core.metadata

import io.bkbn.kompendium.oas.common.ExternalDocumentation
import io.bkbn.kompendium.oas.payload.Parameter
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class GetInfo private constructor(
  val responseType: KType,
  val tags: Set<String>,
  val summary: String,
  val description: String?,
  val externalDocumentation: ExternalDocumentation?,
  val operationId: String?,
  val deprecated: Boolean = false,
  val parameters: List<Parameter> = emptyList()
) {

  companion object {
    fun builder(init: Builder.() -> Unit): GetInfo {
      val builder = Builder()
      builder.init()
      return builder.build()
    }
  }

  class Builder {
    private var responseType: KType? = null
    private var summary: String? = null
    private var description: String? = null
    private var externalDocumentation: ExternalDocumentation? = null
    private var operationId: String? = null
    private var deprecated: Boolean = false
    private var tags: Set<String> = emptySet()
    private var parameters: List<Parameter> = emptyList()

    fun responseType(t: KType) = apply {
      this.responseType = t
    }

    inline fun <reified T> responseType() = apply { responseType(typeOf<T>()) }

    fun summary(s: String) = apply { this.summary = s }

    fun description(s: String) = apply { this.description = s }

    fun externalDocumentation(docs: ExternalDocumentation) = apply { this.externalDocumentation = docs }

    fun operationId(id: String) = apply { this.operationId = id }

    fun isDeprecated() = apply { this.deprecated = true }

    fun tags(vararg tags: String) = apply { this.tags = tags.toSet() }

    fun parameters(vararg parameters: Parameter) = apply { this.parameters = parameters.toList() }

    fun build() = GetInfo(
      responseType = responseType!!,
      summary = summary!!,
      description = description,
      externalDocumentation = externalDocumentation,
      operationId = operationId,
      deprecated = deprecated,
      tags = tags,
      parameters = parameters
    )
  }
}
