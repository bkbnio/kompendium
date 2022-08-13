package io.bkbn.kompendium.core.metadata

import io.bkbn.kompendium.oas.payload.MediaType
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class RequestInfo private constructor(
  val requestType: KType,
  val description: String,
  val examples: Map<String, MediaType.Example>?
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

    fun requestType(t: KType) = apply {
      this.requestType = t
    }

    inline fun <reified T> requestType() = apply { requestType(typeOf<T>()) }

    fun description(s: String) = apply { this.description = s }

    fun examples(vararg e: Pair<String, Any>) = apply {
      this.examples = e.toMap().mapValues { (_, v) -> MediaType.Example(v) }
      println(this.examples)
    }

    fun build() = RequestInfo(
      requestType = requestType!!,
      description = description!!,
      examples = examples
    )
  }

}
