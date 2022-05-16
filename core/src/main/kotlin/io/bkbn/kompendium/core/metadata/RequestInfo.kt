package io.bkbn.kompendium.core.metadata

import kotlin.reflect.KType
import kotlin.reflect.typeOf

class RequestInfo private constructor(
  val requestType: KType,
  val description: String,
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

    fun requestType(t: KType) = apply {
      this.requestType = t
    }

    inline fun <reified T> requestType() = apply { requestType(typeOf<T>()) }

    fun description(s: String) = apply { this.description = s }

    fun build() = RequestInfo(
      requestType = requestType!!,
      description = description!!
    )
  }

}
