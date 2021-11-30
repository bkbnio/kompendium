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
//    Kompendium.openApiSpec.components.schemas.putAll(Kompendium.cache)
    return block.invoke(paramType, requestType, responseType)
  }

  /**
   * Performs all content analysis on the types provided to a notarized error and adds them to the top level spec.
   * @param TErr
   * @param TResp
   * @param block The function to execute, provided type information of the parameters above
   */
  @OptIn(ExperimentalStdlibApi::class)
  inline fun <reified TErr : Throwable, reified TResp : Any> errorNotarizationPreFlight(
    block: (KType, KType) -> Unit
  ) {
    val errorType = typeOf<TErr>()
    val responseType = typeOf<TResp>()
//    addToCache(typeOf<Unit>(), typeOf<Unit>(), responseType)
    return block.invoke(errorType, responseType)
  }

  fun addToCache(paramType: KType, requestType: KType, responseType: KType, feature: Kompendium) {
    feature.config.cache = Kontent.generateKontent(requestType, feature.config.cache)
    feature.config.cache = Kontent.generateKontent(responseType, feature.config.cache)
    feature.config.cache = Kontent.generateParameterKontent(paramType, feature.config.cache)
  }
}
