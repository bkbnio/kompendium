package org.leafygreens.kompendium

import io.ktor.application.ApplicationCall
import io.ktor.features.StatusPages
import io.ktor.http.HttpMethod
import io.ktor.routing.Route
import io.ktor.routing.method
import io.ktor.util.pipeline.PipelineContext
import io.ktor.util.pipeline.PipelineInterceptor
import org.leafygreens.kompendium.Kompendium.parseErrorInfo
import org.leafygreens.kompendium.Kompendium.parseMethodInfo
import org.leafygreens.kompendium.KompendiumPreFlight.errorNotarizationPreFlight
import org.leafygreens.kompendium.models.meta.MethodInfo
import org.leafygreens.kompendium.models.meta.ResponseInfo
import org.leafygreens.kompendium.models.oas.OpenApiSpecPathItem

object Notarized {

  @OptIn(ExperimentalStdlibApi::class)
  inline fun <reified TParam : Any, reified TResp : Any> Route.notarizedGet(
    info: MethodInfo<TParam, Unit, TResp>,
    noinline body: PipelineInterceptor<Unit, ApplicationCall>
  ): Route =
    KompendiumPreFlight.methodNotarizationPreFlight<TParam, Unit, TResp>() { paramType, requestType, responseType ->
      val path = Kompendium.pathCalculator.calculate(this)
      Kompendium.openApiSpec.paths.getOrPut(path) { OpenApiSpecPathItem() }
      Kompendium.openApiSpec.paths[path]?.get =
        info.parseMethodInfo(HttpMethod.Get, paramType, requestType, responseType)
      return method(HttpMethod.Get) { handle(body) }
    }

  inline fun <reified TParam : Any, reified TReq : Any, reified TResp : Any> Route.notarizedPost(
    info: MethodInfo<TParam, TReq, TResp>,
    noinline body: PipelineInterceptor<Unit, ApplicationCall>
  ): Route =
    KompendiumPreFlight.methodNotarizationPreFlight<TParam, TReq, TResp>() { paramType, requestType, responseType ->
      val path = Kompendium.pathCalculator.calculate(this)
      Kompendium.openApiSpec.paths.getOrPut(path) { OpenApiSpecPathItem() }
      Kompendium.openApiSpec.paths[path]?.post =
        info.parseMethodInfo(HttpMethod.Post, paramType, requestType, responseType)
      return method(HttpMethod.Post) { handle(body) }
    }

  inline fun <reified TParam : Any, reified TReq : Any, reified TResp : Any> Route.notarizedPut(
    info: MethodInfo<TParam, TReq, TResp>,
    noinline body: PipelineInterceptor<Unit, ApplicationCall>,
  ): Route =
    KompendiumPreFlight.methodNotarizationPreFlight<TParam, TReq, TResp>() { paramType, requestType, responseType ->
      val path = Kompendium.pathCalculator.calculate(this)
      Kompendium.openApiSpec.paths.getOrPut(path) { OpenApiSpecPathItem() }
      Kompendium.openApiSpec.paths[path]?.put =
        info.parseMethodInfo(HttpMethod.Put, paramType, requestType, responseType)
      return method(HttpMethod.Put) { handle(body) }
    }

  inline fun <reified TParam : Any, reified TResp : Any> Route.notarizedDelete(
    info: MethodInfo<TParam, Unit, TResp>,
    noinline body: PipelineInterceptor<Unit, ApplicationCall>
  ): Route =
    KompendiumPreFlight.methodNotarizationPreFlight<TParam, Unit, TResp> { paramType, requestType, responseType ->
      val path = Kompendium.pathCalculator.calculate(this)
      Kompendium.openApiSpec.paths.getOrPut(path) { OpenApiSpecPathItem() }
      Kompendium.openApiSpec.paths[path]?.delete =
        info.parseMethodInfo(HttpMethod.Delete, paramType, requestType, responseType)
      return method(HttpMethod.Delete) { handle(body) }
    }

  inline fun <reified TErr : Throwable, reified TResp : Any> StatusPages.Configuration.notarizedException(
    info: ResponseInfo<TResp>,
    noinline handler: suspend PipelineContext<Unit, ApplicationCall>.(TErr) -> Unit
  ) = errorNotarizationPreFlight<TErr, TResp>() { errorType, responseType ->
    info.parseErrorInfo(errorType, responseType)
    exception(handler)
  }

}
