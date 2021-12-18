package io.bkbn.kompendium.core

import io.ktor.application.feature
import io.ktor.routing.Route
import io.ktor.routing.application
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Functions are considered preflight when they are used to intercept a method ahead of running.
 */
object KompendiumPreFlight {

  /**
   * Performs all content analysis on the types provided to a notarized route and adds it to the top level spec
   * @param TParam
   * @param TReq
   * @param TResp
   * @param block The function to execute, provided type information of the parameters above
   * @return [Route]
   */
  @OptIn(ExperimentalStdlibApi::class)
  inline fun <reified TParam : Any, reified TReq : Any, reified TResp : Any> Route.methodNotarizationPreFlight(
    block: (KType, KType, KType) -> Route
  ): Route {
    val feature = this.application.feature(Kompendium)
    val requestType = typeOf<TReq>()
    val responseType = typeOf<TResp>()
    val paramType = typeOf<TParam>()
    addToCache(paramType, requestType, responseType, feature)
    return block.invoke(paramType, requestType, responseType)
  }

  fun addToCache(paramType: KType, requestType: KType, responseType: KType, feature: Kompendium) {
    feature.config.cache = Kontent.generateKontent(requestType, feature.config.cache)
    feature.config.cache = Kontent.generateKontent(responseType, feature.config.cache)
    feature.config.cache = Kontent.generateParameterKontent(paramType, feature.config.cache)
  }
}
