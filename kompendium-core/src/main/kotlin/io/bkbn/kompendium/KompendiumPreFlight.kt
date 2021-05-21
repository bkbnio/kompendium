package io.bkbn.kompendium

import io.ktor.routing.Route
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType
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
    val requestType = typeOf<TReq>()
    val responseType = typeOf<TResp>()
    val paramType = typeOf<TParam>()
    addToCache(paramType, requestType, responseType)
    Kompendium.openApiSpec.components.schemas.putAll(Kompendium.cache)
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
    addToCache(typeOf<Unit>(), typeOf<Unit>(), responseType)
    Kompendium.openApiSpec.components.schemas.putAll(Kompendium.cache)
    return block.invoke(errorType, responseType)
  }

  fun addToCache(paramType: KType, requestType: KType, responseType: KType) {
    gatherSubTypes(requestType).forEach {
      Kompendium.cache = Kontent.generateKontent(it, Kompendium.cache)
    }
    gatherSubTypes(responseType).forEach {
      Kompendium.cache = Kontent.generateKontent(it, Kompendium.cache)
    }
    Kompendium.cache = Kontent.generateParameterKontent(paramType, Kompendium.cache)
  }

  fun gatherSubTypes(type: KType): List<KType> {
    val classifier = type.classifier as KClass<*>
    return if (classifier.isSealed) {
      classifier.sealedSubclasses.map {
        it.createType(type.arguments)
      }
    } else {
      listOf(type)
    }
  }
}
