package io.bkbn.kompendium.locations

import io.bkbn.kompendium.Kompendium
import io.bkbn.kompendium.KompendiumPreFlight
import io.bkbn.kompendium.KompendiumPreFlight.errorNotarizationPreFlight
import io.bkbn.kompendium.MethodParser.parseErrorInfo
import io.bkbn.kompendium.MethodParser.parseMethodInfo
import io.bkbn.kompendium.models.meta.MethodInfo
import io.bkbn.kompendium.models.meta.MethodInfo.DeleteInfo
import io.bkbn.kompendium.models.meta.MethodInfo.GetInfo
import io.bkbn.kompendium.models.meta.MethodInfo.PostInfo
import io.bkbn.kompendium.models.meta.MethodInfo.PutInfo
import io.bkbn.kompendium.models.meta.ResponseInfo
import io.bkbn.kompendium.models.oas.OpenApiSpecPathItem
import io.ktor.application.ApplicationCall
import io.ktor.features.StatusPages
import io.ktor.http.HttpMethod
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.routing.Route
import io.ktor.routing.method
import io.ktor.util.pipeline.PipelineContext
import io.ktor.util.pipeline.PipelineInterceptor
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation

/**
 * Notarization methods are the primary way that a Ktor API using Kompendium differentiates
 * from a default Ktor application. On instantiation, a notarized route, provided with the proper metadata,
 * will reflectively analyze all pertinent data to build a corresponding OpenAPI entry.
 */
@KtorExperimentalLocationsAPI
object NotarizedLocation {

  /**
   * Notarization for an HTTP GET request
   * @param TParam The class containing all parameter fields.
   * Each field must be annotated with @[io.bkbn.kompendium.annotations.KompendiumField]
   * @param TResp Class detailing the expected API response
   * @param info Route metadata
   */
  inline fun <reified TParam : Any, reified TResp : Any> Route.notarizedGet(
    info: GetInfo<TParam, TResp>,
    noinline body: PipelineInterceptor<Unit, ApplicationCall>
  ): Route =
    KompendiumPreFlight.methodNotarizationPreFlight<TParam, Unit, TResp>() { paramType, requestType, responseType ->
      val locationAnnotation = TParam::class.findAnnotation<Location>()
      require(locationAnnotation != null) { "Location annotation must be present to leverage notarized location api" }
      val path = Kompendium.calculatePath(this) // TODO Going to require custom path calculation
      val pathWithLocation = path.plus(locationAnnotation.path)
      Kompendium.openApiSpec.paths.getOrPut(pathWithLocation) { OpenApiSpecPathItem() }
      Kompendium.openApiSpec.paths[pathWithLocation]?.get = parseMethodInfo(info, paramType, requestType, responseType)
      return method(HttpMethod.Get) { handle(body) }
    }

  /**
   * Notarization for an HTTP POST request
   * @param TParam The class containing all parameter fields.
   * Each field must be annotated with @[io.bkbn.kompendium.annotations.KompendiumField]
   * @param TReq Class detailing the expected API request body
   * @param TResp Class detailing the expected API response
   * @param info Route metadata
   */
  inline fun <reified TParam : Any, reified TReq : Any, reified TResp : Any> Route.notarizedPost(
    info: PostInfo<TParam, TReq, TResp>,
    noinline body: PipelineInterceptor<Unit, ApplicationCall>
  ): Route =
    KompendiumPreFlight.methodNotarizationPreFlight<TParam, TReq, TResp>() { paramType, requestType, responseType ->
      val locationAnnotation = TParam::class.findAnnotation<Location>()
      require(locationAnnotation != null) { "Location annotation must be present to leverage notarized location api" }
      val path = Kompendium.calculatePath(this)
      val pathWithLocation = path.plus(locationAnnotation.path)
      Kompendium.openApiSpec.paths.getOrPut(pathWithLocation) { OpenApiSpecPathItem() }
      Kompendium.openApiSpec.paths[pathWithLocation]?.post = parseMethodInfo(info, paramType, requestType, responseType)
      return method(HttpMethod.Post) { handle(body) }
    }

  /**
   * Notarization for an HTTP Delete request
   * @param TParam The class containing all parameter fields.
   * Each field must be annotated with @[io.bkbn.kompendium.annotations.KompendiumField]
   * @param TReq Class detailing the expected API request body
   * @param TResp Class detailing the expected API response
   * @param info Route metadata
   */
  inline fun <reified TParam : Any, reified TReq : Any, reified TResp : Any> Route.notarizedPut(
    info: PutInfo<TParam, TReq, TResp>,
    noinline body: PipelineInterceptor<Unit, ApplicationCall>,
  ): Route =
    KompendiumPreFlight.methodNotarizationPreFlight<TParam, TReq, TResp>() { paramType, requestType, responseType ->
      val locationAnnotation = TParam::class.findAnnotation<Location>()
      require(locationAnnotation != null) { "Location annotation must be present to leverage notarized location api" }
      val path = Kompendium.calculatePath(this)
      val pathWithLocation = path.plus(locationAnnotation.path)
      Kompendium.openApiSpec.paths.getOrPut(pathWithLocation) { OpenApiSpecPathItem() }
      Kompendium.openApiSpec.paths[pathWithLocation]?.put =
        parseMethodInfo(info, paramType, requestType, responseType)
      return method(HttpMethod.Put) { handle(body) }
    }

  /**
   * Notarization for an HTTP POST request
   * @param TParam The class containing all parameter fields.
   * Each field must be annotated with @[io.bkbn.kompendium.annotations.KompendiumField]
   * @param TResp Class detailing the expected API response
   * @param info Route metadata
   */
  inline fun <reified TParam : Any, reified TResp : Any> Route.notarizedDelete(
    info: DeleteInfo<TParam, TResp>,
    noinline body: PipelineInterceptor<Unit, ApplicationCall>
  ): Route =
    KompendiumPreFlight.methodNotarizationPreFlight<TParam, Unit, TResp> { paramType, requestType, responseType ->
      val locationAnnotation = TParam::class.findAnnotation<Location>()
      require(locationAnnotation != null) { "Location annotation must be present to leverage notarized location api" }
      val path = Kompendium.calculatePath(this)
      val pathWithLocation = path.plus(locationAnnotation.path)
      Kompendium.openApiSpec.paths.getOrPut(pathWithLocation) { OpenApiSpecPathItem() }
      Kompendium.openApiSpec.paths[pathWithLocation]?.delete = parseMethodInfo(info, paramType, requestType, responseType)
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
