package io.bkbn.kompendium.core.metadata

import io.bkbn.kompendium.oas.payload.MediaType
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class RequestInfo private constructor(
  val requestType: KType,
  val description: String,
  val examples: Map<String, MediaType.Example>?,
  val mediaTypes: Set<String>
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
    private var description: String? = null
    private var examples: Map<String, MediaType.Example>? = null
    private var mediaTypes: Set<String>? = null

    fun requestType(t: KType) = apply {
      this.requestType = t
    }

    inline fun <reified T> requestType() = apply { requestType(typeOf<T>()) }

    fun description(s: String) = apply { this.description = s }

    fun examples(vararg e: Pair<String, Any>) = apply {
      this.examples = e.toMap().mapValues { (_, v) -> MediaType.Example(v) }
    }

    fun mediaTypes(vararg m: String) = apply {
      this.mediaTypes = m.toSet()
    }

    fun build() = RequestInfo(
      requestType = requestType ?: error("Request type must be present"),
      description = description ?: error("Description must be present"),
      examples = examples,
      mediaTypes = mediaTypes ?: setOf("application/json")
    )
  }
}
