package io.bkbn.kompendium.core.metadata

import io.bkbn.kompendium.enrichment.TypeEnrichment
import io.bkbn.kompendium.oas.payload.MediaType
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class RequestInfo private constructor(
  val requestType: KType,
  val enrichment: TypeEnrichment<*>?,
  val description: String,
  val examples: Map<String, MediaType.Example>?,
  val mediaTypes: Set<String>,
  val required: Boolean
) {

  companion object {
    fun builder(init: Builder.() -> Unit): RequestInfo {
      val builder = Builder()
      builder.init()
      return builder.build()
    }
  }

  class Builder {
    private var requestType: KType? = null
    private var typeEnrichment: TypeEnrichment<*>? = null
    private var description: String? = null
    private var examples: Map<String, MediaType.Example>? = null
    private var mediaTypes: Set<String>? = null
    private var required: Boolean? = null

    fun required(r: Boolean) = apply {
      this.required = r
    }

    fun requestType(t: KType) = apply {
      this.requestType = t
    }

    fun enrichment(t: TypeEnrichment<*>) = apply {
      this.typeEnrichment = t
    }

    inline fun <reified T> requestType(enrichment: TypeEnrichment<T>? = null) = apply {
      requestType(typeOf<T>())
      enrichment?.let { enrichment(it) }
    }

    fun description(s: String) = apply { this.description = s }

    fun examples(vararg e: Pair<String, MediaType.Example>) = apply {
      this.examples = e.toMap()
    }

    fun mediaTypes(vararg m: String) = apply {
      this.mediaTypes = m.toSet()
    }

    fun build() = RequestInfo(
      requestType = requestType ?: error("Request type must be present"),
      description = description ?: error("Description must be present"),
      enrichment = typeEnrichment,
      examples = examples,
      mediaTypes = mediaTypes ?: setOf("application/json"),
      required = required ?: true
    )
  }
}
