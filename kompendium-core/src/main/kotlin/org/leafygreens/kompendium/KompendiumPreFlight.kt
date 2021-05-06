package org.leafygreens.kompendium

import io.ktor.routing.Route
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
  inline fun <reified TParam : Any, reified TReq : Any, reified TResp : Any> methodNotarizationPreFlight(
    block: (KType, KType, KType) -> Route
  ): Route {
    Kompendium.cache = Kontent.generateKontent<TResp>(Kompendium.cache)
    Kompendium.cache = Kontent.generateKontent<TReq>(Kompendium.cache)
    Kompendium.cache = Kontent.generateParameterKontent<TParam>(Kompendium.cache)
    Kompendium.openApiSpec.components.schemas.putAll(Kompendium.cache)
    val requestType = typeOf<TReq>()
    val responseType = typeOf<TResp>()
    val paramType = typeOf<TParam>()
    return block.invoke(paramType, requestType, responseType)
  }

  /**
   * Performs all content analysis on the types provided to a notarized error and adds them to the top level spec.
   * @param TErr
   * @param TResp
   * @param block The function to execute, provided type information of the parameters above
   */
  @OptIn(ExperimentalStdlibApi::class)
  inline fun <reified TErr: Throwable, reified TResp : Any> errorNotarizationPreFlight(
    block: (KType, KType) -> Unit
  ) {
    Kompendium.cache = Kontent.generateKontent<TResp>(Kompendium.cache)
    Kompendium.openApiSpec.components.schemas.putAll(Kompendium.cache)
    val errorType = typeOf<TErr>()
    val responseType = typeOf<TResp>()
    return block.invoke(errorType, responseType)
  }
}
