package org.leafygreens.kompendium

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpMethod
import io.ktor.routing.Route
import io.ktor.routing.method
import io.ktor.util.pipeline.PipelineInterceptor
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.typeOf
import org.leafygreens.kompendium.Kontent.generateKontent
import org.leafygreens.kompendium.annotations.KompendiumRequest
import org.leafygreens.kompendium.annotations.KompendiumResponse
import org.leafygreens.kompendium.models.meta.MethodInfo
import org.leafygreens.kompendium.models.oas.OpenApiSpec
import org.leafygreens.kompendium.models.oas.OpenApiSpecInfo
import org.leafygreens.kompendium.models.oas.OpenApiSpecMediaType
import org.leafygreens.kompendium.models.oas.OpenApiSpecPathItem
import org.leafygreens.kompendium.models.oas.OpenApiSpecPathItemOperation
import org.leafygreens.kompendium.models.oas.OpenApiSpecReferenceObject
import org.leafygreens.kompendium.models.oas.OpenApiSpecRequest
import org.leafygreens.kompendium.models.oas.OpenApiSpecResponse
import org.leafygreens.kompendium.util.Helpers.calculatePath
import org.leafygreens.kompendium.util.Helpers.getReferenceSlug
import org.leafygreens.kompendium.util.KompendiumHttpCodes

object Kompendium {

  var openApiSpec = OpenApiSpec(
    info = OpenApiSpecInfo(),
    servers = mutableListOf(),
    paths = mutableMapOf()
  )

  @OptIn(ExperimentalStdlibApi::class)
  inline fun <reified TParam : Any, reified TResp : Any> Route.notarizedGet(
    info: MethodInfo,
    noinline body: PipelineInterceptor<Unit, ApplicationCall>
  ): Route = notarizationPreFlight<Unit, TResp>() { requestType, responseType ->
    val path = calculatePath()
    openApiSpec.paths.getOrPut(path) { OpenApiSpecPathItem() }
    openApiSpec.paths[path]?.get = info.parseMethodInfo(HttpMethod.Get, requestType, responseType)
    return method(HttpMethod.Get) { handle(body) }
  }

  inline fun <reified TParam : Any, reified TReq : Any, reified TResp : Any> Route.notarizedPost(
    info: MethodInfo,
    noinline body: PipelineInterceptor<Unit, ApplicationCall>
  ): Route = notarizationPreFlight<TReq, TResp>() { requestType, responseType ->
    val path = calculatePath()
    openApiSpec.paths.getOrPut(path) { OpenApiSpecPathItem() }
    openApiSpec.paths[path]?.post = info.parseMethodInfo(HttpMethod.Post, requestType, responseType)
    return method(HttpMethod.Post) { handle(body) }
  }

  inline fun <reified TParam : Any, reified TReq : Any, reified TResp : Any> Route.notarizedPut(
    info: MethodInfo,
    noinline body: PipelineInterceptor<Unit, ApplicationCall>,
  ): Route = notarizationPreFlight<TReq, TResp>() { requestType, responseType ->
    val path = calculatePath()
    openApiSpec.paths.getOrPut(path) { OpenApiSpecPathItem() }
    openApiSpec.paths[path]?.put = info.parseMethodInfo(HttpMethod.Put, requestType, responseType)
    return method(HttpMethod.Put) { handle(body) }
  }

  inline fun <reified TParam : Any, reified TResp : Any> Route.notarizedDelete(
    info: MethodInfo,
    noinline body: PipelineInterceptor<Unit, ApplicationCall>
  ): Route = notarizationPreFlight<Unit, TResp> { requestType, responseType ->
    val path = calculatePath()
    openApiSpec.paths.getOrPut(path) { OpenApiSpecPathItem() }
    openApiSpec.paths[path]?.delete = info.parseMethodInfo(HttpMethod.Delete, requestType, responseType)
    return method(HttpMethod.Delete) { handle(body) }
  }

  fun MethodInfo.parseMethodInfo(
    method: HttpMethod,
    requestType: KType,
    responseType: KType
  ) = OpenApiSpecPathItemOperation(
    summary = this.summary,
    description = this.description,
    tags = this.tags,
    deprecated = this.deprecated,
    responses = parseResponseAnnotation(responseType)?.let { mapOf(it) },
    requestBody = if (method != HttpMethod.Get) parseRequestAnnotation(requestType) else null
  )

  @OptIn(ExperimentalStdlibApi::class)
  inline fun <reified TReq : Any, reified TResp : Any> notarizationPreFlight(
    block: (KType, KType) -> Route
  ): Route {
    val responseKontent = generateKontent<TResp>()
    val requestKontent = generateKontent<TReq>()
    openApiSpec.components.schemas.putAll(responseKontent)
    openApiSpec.components.schemas.putAll(requestKontent)
    val requestType = typeOf<TReq>()
    val responseType = typeOf<TResp>()
    return block.invoke(requestType, responseType)
  }

  private fun parseRequestAnnotation(requestType: KType): OpenApiSpecRequest? = when (requestType) {
    Unit::class -> null
    else -> when (val anny = requestType.findAnnotation<KompendiumRequest>()) {
      // TODO Need to be smarter here... reuse kontent generator... or maybe have kontent return top level ref?
      null -> OpenApiSpecRequest(
        description = "placeholder",
        content = setOf("application/json").associateWith {
          val ref = requestType.getReferenceSlug()
          val mediaType = OpenApiSpecMediaType.Referenced(OpenApiSpecReferenceObject(ref))
          mediaType
        }
      )
      else -> OpenApiSpecRequest(
        description = anny.description,
        content = anny.mediaTypes.associate {
          val ref = requestType.getReferenceSlug()
          val mediaType = OpenApiSpecMediaType.Referenced(OpenApiSpecReferenceObject(ref))
          Pair(it, mediaType)
        }
      )
    }
  }

  private fun parseResponseAnnotation(responseType: KType): Pair<Int, OpenApiSpecResponse>? = when (responseType) {
    Unit::class -> null
    else -> when (val anny = responseType.findAnnotation<KompendiumResponse>()) {
      null -> {
        val specResponse = OpenApiSpecResponse(
          description = "placeholder",
          content = setOf("application/json").associateWith {
            val ref = responseType.getReferenceSlug()
            val mediaType = OpenApiSpecMediaType.Referenced(OpenApiSpecReferenceObject(ref))
            mediaType
          }
        )
        Pair(KompendiumHttpCodes.OK, specResponse)
      }
      else -> {
        val specResponse = OpenApiSpecResponse(
          description = anny.description,
          content = anny.mediaTypes.associate {
            val ref = responseType.getReferenceSlug()
            val mediaType = OpenApiSpecMediaType.Referenced(OpenApiSpecReferenceObject(ref))
            Pair(it, mediaType)
          }
        )
        Pair(anny.status, specResponse)
      }
    }
  }

  internal fun resetSchema() {
    openApiSpec = OpenApiSpec(
      info = OpenApiSpecInfo(),
      servers = mutableListOf(),
      paths = mutableMapOf()
    )
  }
}
