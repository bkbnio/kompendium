package io.bkbn.kompendium.core

import io.bkbn.kompendium.annotations.Param
import io.bkbn.kompendium.core.KompendiumPreFlight.methodNotarizationPreFlight
import io.bkbn.kompendium.core.MethodParser.parseMethodInfo
import io.bkbn.kompendium.core.metadata.method.DeleteInfo
import io.bkbn.kompendium.core.metadata.method.GetInfo
import io.bkbn.kompendium.core.metadata.method.PostInfo
import io.bkbn.kompendium.core.metadata.method.PutInfo
import io.bkbn.kompendium.oas.path.Path
import io.bkbn.kompendium.oas.path.PathOperation
import io.ktor.application.ApplicationCall
import io.ktor.application.feature
import io.ktor.http.HttpMethod
import io.ktor.routing.Route
import io.ktor.routing.application
import io.ktor.routing.method
import io.ktor.util.pipeline.PipelineInterceptor

/**
 * Notarization methods are the primary way that a Ktor API using Kompendium differentiates
 * from a default Ktor application. On instantiation, a notarized route, provided with the proper metadata,
 * will reflectively analyze all pertinent data to build a corresponding OpenAPI entry.
 */
object Notarized {

  /**
   * Notarization for an HTTP GET request
   * @param TParam The class containing all parameter fields. Each field must be annotated with @[Param]
   * @param TResp Class detailing the expected API response
   * @param info Route metadata
   * @param postProcess Adds an optional callback hook to perform manual overrides on the generated [PathOperation]
   */
  inline fun <reified TParam : Any, reified TResp : Any> Route.notarizedGet(
    info: GetInfo<TParam, TResp>,
    postProcess: (PathOperation) -> PathOperation = { p -> p },
    noinline body: PipelineInterceptor<Unit, ApplicationCall>
  ): Route = methodNotarizationPreFlight<TParam, Unit, TResp> { paramType, requestType, responseType ->
    val feature = this.application.feature(Kompendium)
    val path = calculateRoutePath()
    feature.config.spec.paths.getOrPut(path) { Path() }
    val baseInfo = parseMethodInfo(info, paramType, requestType, responseType, feature)
    feature.config.spec.paths[path]?.get = postProcess(baseInfo)
    return method(HttpMethod.Get) { handle(body) }
  }

  /**
   * Notarization for an HTTP POST request
   * @param TParam The class containing all parameter fields. Each field must be annotated with @[Param]
   * @param TReq Class detailing the expected API request body
   * @param TResp Class detailing the expected API response
   * @param info Route metadata
   * @param postProcess Adds an optional callback hook to perform manual overrides on the generated [PathOperation]
   */
  inline fun <reified TParam : Any, reified TReq : Any, reified TResp : Any> Route.notarizedPost(
    info: PostInfo<TParam, TReq, TResp>,
    postProcess: (PathOperation) -> PathOperation = { p -> p },
    noinline body: PipelineInterceptor<Unit, ApplicationCall>
  ): Route = methodNotarizationPreFlight<TParam, TReq, TResp> { paramType, requestType, responseType ->
    val feature = this.application.feature(Kompendium)
    val path = calculateRoutePath()
    feature.config.spec.paths.getOrPut(path) { Path() }
    val baseInfo = parseMethodInfo(info, paramType, requestType, responseType, feature)
    feature.config.spec.paths[path]?.post = postProcess(baseInfo)
    return method(HttpMethod.Post) { handle(body) }
  }

  /**
   * Notarization for an HTTP Delete request
   * @param TParam The class containing all parameter fields. Each field must be annotated with @[Param]
   * @param TReq Class detailing the expected API request body
   * @param TResp Class detailing the expected API response
   * @param info Route metadata
   * @param postProcess Adds an optional callback hook to perform manual overrides on the generated [PathOperation]
   */
  inline fun <reified TParam : Any, reified TReq : Any, reified TResp : Any> Route.notarizedPut(
    info: PutInfo<TParam, TReq, TResp>,
    postProcess: (PathOperation) -> PathOperation = { p -> p },
    noinline body: PipelineInterceptor<Unit, ApplicationCall>,
  ): Route = methodNotarizationPreFlight<TParam, TReq, TResp> { paramType, requestType, responseType ->
    val feature = this.application.feature(Kompendium)
    val path = calculateRoutePath()
    feature.config.spec.paths.getOrPut(path) { Path() }
    val baseInfo = parseMethodInfo(info, paramType, requestType, responseType, feature)
    feature.config.spec.paths[path]?.put = postProcess(baseInfo)
    return method(HttpMethod.Put) { handle(body) }
  }

  /**
   * Notarization for an HTTP POST request
   * @param TParam The class containing all parameter fields. Each field must be annotated with @[Param]
   * @param TResp Class detailing the expected API response
   * @param info Route metadata
   * @param postProcess Adds an optional callback hook to perform manual overrides on the generated [PathOperation]
   */
  inline fun <reified TParam : Any, reified TResp : Any> Route.notarizedDelete(
    info: DeleteInfo<TParam, TResp>,
    postProcess: (PathOperation) -> PathOperation = { p -> p },
    noinline body: PipelineInterceptor<Unit, ApplicationCall>
  ): Route = methodNotarizationPreFlight<TParam, Unit, TResp> { paramType, requestType, responseType ->
    val feature = this.application.feature(Kompendium)
    val path = calculateRoutePath()
    feature.config.spec.paths.getOrPut(path) { Path() }
    val baseInfo = parseMethodInfo(info, paramType, requestType, responseType, feature)
    feature.config.spec.paths[path]?.delete = postProcess(baseInfo)
    return method(HttpMethod.Delete) { handle(body) }
  }

  /**
   * Uses the built-in Ktor route path [Route.toString] but cuts out any meta route such as authentication... anything
   * that matches the RegEx pattern `/\\(.+\\)`
   */
  fun Route.calculateRoutePath() = toString().replace(Regex("/\\(.+\\)"), "")
}
