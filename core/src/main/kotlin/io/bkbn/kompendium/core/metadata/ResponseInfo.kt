package io.bkbn.kompendium.core.metadata

import io.bkbn.kompendium.enrichment.TypeEnrichment
import io.bkbn.kompendium.oas.payload.Header
import io.bkbn.kompendium.oas.payload.MediaType
import io.ktor.http.HttpStatusCode
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class ResponseInfo private constructor(
  val responseCode: HttpStatusCode,
  val responseType: KType,
  val enrichment: TypeEnrichment<*>?,
  val description: String,
  val examples: Map<String, MediaType.Example>?,
  val mediaTypes: Set<String>,
  val responseHeaders: Map<String, Header>?
) {

  companion object {
    fun builder(init: Builder.() -> Unit): ResponseInfo {
      val builder = Builder()
      builder.init()
      return builder.build()
    }
  }

  class Builder {
    private var responseCode: HttpStatusCode? = null
    private var responseType: KType? = null
    private var typeEnrichment: TypeEnrichment<*>? = null
    private var description: String? = null
    private var examples: Map<String, MediaType.Example>? = null
    private var mediaTypes: Set<String>? = null
    private var responseHeaders: Map<String, Header>? = null

    fun responseHeaders(headers: Map<String, Header>) = apply {
      this.responseHeaders = headers
    }

    fun responseCode(code: HttpStatusCode) = apply {
      this.responseCode = code
    }

    fun responseType(t: KType) = apply {
      this.responseType = t
    }

    fun enrichment(t: TypeEnrichment<*>) = apply {
      this.typeEnrichment = t
    }

    inline fun <reified T> responseType(enrichment: TypeEnrichment<T>? = null) = apply {
      responseType(typeOf<T>())
      enrichment?.let { enrichment(it) }
    }

    fun description(s: String) = apply { this.description = s }

    fun examples(vararg e: Pair<String, MediaType.Example>) = apply {
      this.examples = e.toMap()
    }

    fun mediaTypes(vararg m: String) = apply {
      this.mediaTypes = m.toSet()
    }

    fun build() = ResponseInfo(
      responseCode = responseCode ?: error("You must provide a response code in order to build a Response!"),
      responseType = responseType ?: error("You must provide a response type in order to build a Response!"),
      description = description ?: error("You must provide a description in order to build a Response!"),
      enrichment = typeEnrichment,
      examples = examples,
      mediaTypes = mediaTypes ?: setOf("application/json"),
      responseHeaders = responseHeaders
    )
  }
}
