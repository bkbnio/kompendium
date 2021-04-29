package org.leafygreens.kompendium

import io.ktor.routing.Route
import kotlin.reflect.KType
import kotlin.reflect.typeOf

object KompendiumPreFlight {

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
