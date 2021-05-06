package org.leafygreens.kompendium

import io.ktor.application.ApplicationCall
import io.ktor.features.StatusPages
import io.ktor.http.HttpMethod
import io.ktor.routing.Route
import io.ktor.routing.method
import io.ktor.util.pipeline.PipelineContext
import io.ktor.util.pipeline.PipelineInterceptor
import org.leafygreens.kompendium.KompendiumPreFlight.errorNotarizationPreFlight
import org.leafygreens.kompendium.MethodParser.parseErrorInfo
import org.leafygreens.kompendium.MethodParser.parseMethodInfo
import org.leafygreens.kompendium.models.meta.MethodInfo.GetInfo
import org.leafygreens.kompendium.models.meta.MethodInfo.PostInfo
import org.leafygreens.kompendium.models.meta.MethodInfo.PutInfo
import org.leafygreens.kompendium.models.meta.MethodInfo.DeleteInfo
import org.leafygreens.kompendium.models.meta.ResponseInfo
import org.leafygreens.kompendium.models.oas.OpenApiSpecPathItem

/**
 * Notarization methods are the primary way that a Ktor API using Kompendium differentiates
 * from a default Ktor application. On instantiation, a notarized route, provided with the proper metadata,
 * will reflectively analyze all pertinent data to build a corresponding OpenAPI entry.
 */
object Notarized {

  /**
   * Notarization for an HTTP GET request
   * @param TParam The class containing all parameter fields.
   * Each field must be annotated with @[org.leafygreens.kompendium.annotations.KompendiumField]
   * @param TResp Class detailing the expected API response
   * @param info Route metadata
   */
  inline fun <reified TParam : Any, reified TResp : Any> Route.notarizedGet(
    info: GetInfo<TParam, TResp>,
    noinline body: PipelineInterceptor<Unit, ApplicationCall>
  ): Route =
    KompendiumPreFlight.methodNotarizationPreFlight<TParam, Unit, TResp>() { paramType, requestType, responseType ->
      val path = Kompendium.pathCalculator.calculate(this)
      Kompendium.openApiSpec.paths.getOrPut(path) { OpenApiSpecPathItem() }
      Kompendium.openApiSpec.paths[path]?.get = parseMethodInfo(info, paramType, requestType, responseType)
      return method(HttpMethod.Get) { handle(body) }
    }

  /**
   * Notarization for an HTTP POST request
   * @param TParam The class containing all parameter fields.
   * Each field must be annotated with @[org.leafygreens.kompendium.annotations.KompendiumField]
   * @param TReq Class detailing the expected API request body
   * @param TResp Class detailing the expected API response
   * @param info Route metadata
   */
  inline fun <reified TParam : Any, reified TReq : Any, reified TResp : Any> Route.notarizedPost(
    info: PostInfo<TParam, TReq, TResp>,
    noinline body: PipelineInterceptor<Unit, ApplicationCall>
  ): Route =
    KompendiumPreFlight.methodNotarizationPreFlight<TParam, TReq, TResp>() { paramType, requestType, responseType ->
      val path = Kompendium.pathCalculator.calculate(this)
      Kompendium.openApiSpec.paths.getOrPut(path) { OpenApiSpecPathItem() }
      Kompendium.openApiSpec.paths[path]?.post = parseMethodInfo(info, paramType, requestType, responseType)
      return method(HttpMethod.Post) { handle(body) }
    }

  /**
   * Notarization for an HTTP Delete request
   * @param TParam The class containing all parameter fields.
   * Each field must be annotated with @[org.leafygreens.kompendium.annotations.KompendiumField]
   * @param TReq Class detailing the expected API request body
   * @param TResp Class detailing the expected API response
   * @param info Route metadata
   */
  inline fun <reified TParam : Any, reified TReq : Any, reified TResp : Any> Route.notarizedPut(
    info: PutInfo<TParam, TReq, TResp>,
    noinline body: PipelineInterceptor<Unit, ApplicationCall>,
  ): Route =
    KompendiumPreFlight.methodNotarizationPreFlight<TParam, TReq, TResp>() { paramType, requestType, responseType ->
      val path = Kompendium.pathCalculator.calculate(this)
      Kompendium.openApiSpec.paths.getOrPut(path) { OpenApiSpecPathItem() }
      Kompendium.openApiSpec.paths[path]?.put =
        parseMethodInfo(info, paramType, requestType, responseType)
      return method(HttpMethod.Put) { handle(body) }
    }

  /**
   * Notarization for an HTTP POST request
   * @param TParam The class containing all parameter fields.
   * Each field must be annotated with @[org.leafygreens.kompendium.annotations.KompendiumField]
   * @param TResp Class detailing the expected API response
   * @param info Route metadata
   */
  inline fun <reified TParam : Any, reified TResp : Any> Route.notarizedDelete(
    info: DeleteInfo<TParam, TResp>,
    noinline body: PipelineInterceptor<Unit, ApplicationCall>
  ): Route =
    KompendiumPreFlight.methodNotarizationPreFlight<TParam, Unit, TResp> { paramType, requestType, responseType ->
      val path = Kompendium.pathCalculator.calculate(this)
      Kompendium.openApiSpec.paths.getOrPut(path) { OpenApiSpecPathItem() }
      Kompendium.openApiSpec.paths[path]?.delete = parseMethodInfo(info, paramType, requestType, responseType)
      return method(HttpMethod.Delete) { handle(body) }
    }

  /**
   * Notarization for a handled exception response
   * @param TErr The [Throwable] that is being handled
   * @param TResp Class detailing the expected API response when handled
   * @param info Response metadata
   */
  inline fun <reified TErr : Throwable, reified TResp : Any> StatusPages.Configuration.notarizedException(
    info: ResponseInfo<TResp>,
    noinline handler: suspend PipelineContext<Unit, ApplicationCall>.(TErr) -> Unit
  ) = errorNotarizationPreFlight<TErr, TResp>() { errorType, responseType ->
    info.parseErrorInfo(errorType, responseType)
    exception(handler)
  }

}
