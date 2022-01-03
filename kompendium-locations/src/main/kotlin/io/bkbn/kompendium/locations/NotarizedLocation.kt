package io.bkbn.kompendium.locations

import io.bkbn.kompendium.core.Kompendium
import io.bkbn.kompendium.core.KompendiumPreFlight.methodNotarizationPreFlight
import io.bkbn.kompendium.core.MethodParser.parseMethodInfo
import io.bkbn.kompendium.core.Notarized.calculateRoutePath
import io.bkbn.kompendium.core.metadata.method.DeleteInfo
import io.bkbn.kompendium.core.metadata.method.GetInfo
import io.bkbn.kompendium.core.metadata.method.PostInfo
import io.bkbn.kompendium.core.metadata.method.PutInfo
import io.bkbn.kompendium.oas.path.Path
import io.bkbn.kompendium.oas.path.PathOperation
import io.ktor.application.ApplicationCall
import io.ktor.application.feature
import io.ktor.http.HttpMethod
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.handle
import io.ktor.locations.location
import io.ktor.routing.Route
import io.ktor.routing.application
import io.ktor.routing.method
import io.ktor.util.pipeline.PipelineContext
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

/**
 * This version of notarized routes leverages the Ktor [io.ktor.locations.Locations] plugin to provide type safe access
 * to all path and query parameters.
 */
@KtorExperimentalLocationsAPI
object NotarizedLocation {

  /**
   * Notarization for an HTTP GET request leveraging the Ktor [io.ktor.locations.Locations] plugin
   * @param TParam The class containing all parameter fields.
   * Each field must be annotated with @[io.bkbn.kompendium.annotations.Param].
   * Additionally, the class must be annotated with @[io.ktor.locations.Location].
   * @param TResp Class detailing the expected API response
   * @param info Route metadata
   * @param postProcess Adds an optional callback hook to perform manual overrides on the generated [PathOperation]
   */
  inline fun <reified TParam : Any, reified TResp : Any> Route.notarizedGet(
    info: GetInfo<TParam, TResp>,
    postProcess: (PathOperation) -> PathOperation = { p -> p },
    noinline body: suspend PipelineContext<Unit, ApplicationCall>.(TParam) -> Unit
  ): Route = methodNotarizationPreFlight<TParam, Unit, TResp>() { paramType, requestType, responseType ->
    val locationAnnotation = TParam::class.findAnnotation<Location>()
    require(locationAnnotation != null) { "Location annotation must be present to leverage notarized location api" }
    val feature = application.feature(Kompendium)
    val path = calculateRoutePath()
    val locationPath = TParam::class.calculateLocationPath()
    val pathWithLocation = path.plus(locationPath)
    feature.config.spec.paths.getOrPut(pathWithLocation) { Path() }
    val baseInfo = parseMethodInfo(info, paramType, requestType, responseType, feature)
    feature.config.spec.paths[pathWithLocation]?.get = postProcess(baseInfo)
    return location(TParam::class) {
      method(HttpMethod.Get) { handle(body) }
    }
  }

  /**
   * Notarization for an HTTP POST request leveraging the Ktor [io.ktor.locations.Locations] plugin
   * @param TParam The class containing all parameter fields.
   * Each field must be annotated with @[io.bkbn.kompendium.annotations.Param]
   * Additionally, the class must be annotated with @[io.ktor.locations.Location].
   * @param TReq Class detailing the expected API request body
   * @param TResp Class detailing the expected API response
   * @param info Route metadata
   * @param postProcess Adds an optional callback hook to perform manual overrides on the generated [PathOperation]
   */
  inline fun <reified TParam : Any, reified TReq : Any, reified TResp : Any> Route.notarizedPost(
    info: PostInfo<TParam, TReq, TResp>,
    postProcess: (PathOperation) -> PathOperation = { p -> p },
    noinline body: suspend PipelineContext<Unit, ApplicationCall>.(TParam) -> Unit
  ): Route = methodNotarizationPreFlight<TParam, TReq, TResp>() { paramType, requestType, responseType ->
    val locationAnnotation = TParam::class.findAnnotation<Location>()
    require(locationAnnotation != null) { "Location annotation must be present to leverage notarized location api" }
    val feature = application.feature(Kompendium)
    val path = calculateRoutePath()
    val locationPath = TParam::class.calculateLocationPath()
    val pathWithLocation = path.plus(locationPath)
    feature.config.spec.paths.getOrPut(pathWithLocation) { Path() }
    val baseInfo = parseMethodInfo(info, paramType, requestType, responseType, feature)
    feature.config.spec.paths[pathWithLocation]?.post = postProcess(baseInfo)
    return location(TParam::class) {
      method(HttpMethod.Post) { handle(body) }
    }
  }

  /**
   * Notarization for an HTTP Delete request leveraging the Ktor [io.ktor.locations.Locations] plugin
   * @param TParam The class containing all parameter fields.
   * Each field must be annotated with @[io.bkbn.kompendium.annotations.Param]
   * Additionally, the class must be annotated with @[io.ktor.locations.Location].
   * @param TReq Class detailing the expected API request body
   * @param TResp Class detailing the expected API response
   * @param info Route metadata
   * @param postProcess Adds an optional callback hook to perform manual overrides on the generated [PathOperation]
   */
  inline fun <reified TParam : Any, reified TReq : Any, reified TResp : Any> Route.notarizedPut(
    info: PutInfo<TParam, TReq, TResp>,
    postProcess: (PathOperation) -> PathOperation = { p -> p },
    noinline body: suspend PipelineContext<Unit, ApplicationCall>.(TParam) -> Unit
  ): Route = methodNotarizationPreFlight<TParam, TReq, TResp>() { paramType, requestType, responseType ->
    val locationAnnotation = TParam::class.findAnnotation<Location>()
    require(locationAnnotation != null) { "Location annotation must be present to leverage notarized location api" }
    val feature = application.feature(Kompendium)
    val path = calculateRoutePath()
    val locationPath = TParam::class.calculateLocationPath()
    val pathWithLocation = path.plus(locationPath)
    feature.config.spec.paths.getOrPut(pathWithLocation) { Path() }
    val baseInfo = parseMethodInfo(info, paramType, requestType, responseType, feature)
    feature.config.spec.paths[pathWithLocation]?.put = postProcess(baseInfo)
    return location(TParam::class) {
      method(HttpMethod.Put) { handle(body) }
    }
  }

  /**
   * Notarization for an HTTP POST request leveraging the Ktor [io.ktor.locations.Locations] plugin
   * @param TParam The class containing all parameter fields.
   * Each field must be annotated with @[io.bkbn.kompendium.annotations.Param]
   * Additionally, the class must be annotated with @[io.ktor.locations.Location].
   * @param TResp Class detailing the expected API response
   * @param info Route metadata
   * @param postProcess Adds an optional callback hook to perform manual overrides on the generated [PathOperation]
   */
  inline fun <reified TParam : Any, reified TResp : Any> Route.notarizedDelete(
    info: DeleteInfo<TParam, TResp>,
    postProcess: (PathOperation) -> PathOperation = { p -> p },
    noinline body: suspend PipelineContext<Unit, ApplicationCall>.(TParam) -> Unit
  ): Route = methodNotarizationPreFlight<TParam, Unit, TResp> { paramType, requestType, responseType ->
    val locationAnnotation = TParam::class.findAnnotation<Location>()
    require(locationAnnotation != null) { "Location annotation must be present to leverage notarized location api" }
    val feature = application.feature(Kompendium)
    val path = calculateRoutePath()
    val locationPath = TParam::class.calculateLocationPath()
    val pathWithLocation = path.plus(locationPath)
    feature.config.spec.paths.getOrPut(pathWithLocation) { Path() }
    val baseInfo = parseMethodInfo(info, paramType, requestType, responseType, feature)
    feature.config.spec.paths[pathWithLocation]?.delete = postProcess(baseInfo)
    return location(TParam::class) {
      method(HttpMethod.Delete) { handle(body) }
    }
  }

  fun KClass<*>.calculateLocationPath(suffix: String = ""): String {
    val locationAnnotation = this.findAnnotation<Location>()
    require(locationAnnotation != null) { "Location annotation must be present to leverage notarized location api" }
    val parent = this.java.declaringClass?.kotlin
    val newSuffix = locationAnnotation.path.plus(suffix)
    return when (parent) {
      null -> newSuffix
      else -> parent.calculateLocationPath(newSuffix)
    }
  }
}
