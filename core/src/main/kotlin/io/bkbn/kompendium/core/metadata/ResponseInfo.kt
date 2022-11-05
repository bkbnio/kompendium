package io.bkbn.kompendium.core.metadata

import io.bkbn.kompendium.oas.payload.MediaType
import io.ktor.http.HttpStatusCode
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class ResponseInfo private constructor(
  val responseCode: HttpStatusCode,
  val responseType: KType,
  val description: String,
  val examples: Map<String, MediaType.Example>?,
  val mediaTypes: Set<String>
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
    private var description: String? = null
    private var examples: Map<String, MediaType.Example>? = null
    private var mediaTypes: Set<String>? = null

    fun responseCode(code: HttpStatusCode) = apply {
      this.responseCode = code
    }

    fun responseType(t: KType) = apply {
      this.responseType = t
    }

    inline fun <reified T> responseType() = apply { responseType(typeOf<T>()) }

    fun description(s: String) = apply { this.description = s }

    fun examples(vararg e: Pair<String, Any>) = apply {
      this.examples = e.toMap().mapValues { (_, v) -> MediaType.Example(v) }
    }

    fun mediaTypes(vararg m: String) = apply {
      this.mediaTypes = m.toSet()
    }

    fun build() = ResponseInfo(
      responseCode = responseCode ?: error("You must provide a response code in order to build a Response!"),
      responseType = responseType ?: error("You must provide a response type in order to build a Response!"),
      description = description ?: error("You must provide a description in order to build a Response!"),
      examples = examples,
      mediaTypes = mediaTypes ?: setOf("application/json")
    )
  }
}
