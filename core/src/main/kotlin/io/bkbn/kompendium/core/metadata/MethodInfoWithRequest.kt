package io.bkbn.kompendium.core.metadata

sealed interface MethodInfoWithRequest : MethodInfo {
  val request: RequestInfo?

  abstract class Builder<T : MethodInfoWithRequest> : MethodInfo.Builder<T>() {
    internal var request: RequestInfo? = null

    fun request(init: RequestInfo.Builder.() -> Unit) = apply {
      val builder = RequestInfo.Builder()
      builder.init()
      this.request = builder.build()
    }
  }
}
