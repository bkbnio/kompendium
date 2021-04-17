package org.leafygreens.kompendium

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpMethod
import io.ktor.routing.Route
import io.ktor.routing.method
import io.ktor.util.pipeline.PipelineInterceptor
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import org.leafygreens.kompendium.Kontent.generateKontent
import org.leafygreens.kompendium.Kontent.generateParameterKontent
import org.leafygreens.kompendium.models.meta.MethodInfo
import org.leafygreens.kompendium.models.meta.RequestInfo
import org.leafygreens.kompendium.models.meta.ResponseInfo
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
  ): Route = notarizationPreFlight<TParam, Unit, TResp>() { requestType, responseType ->
    val path = calculatePath()
    openApiSpec.paths.getOrPut(path) { OpenApiSpecPathItem() }
    openApiSpec.paths[path]?.get = info.parseMethodInfo(HttpMethod.Get, requestType, responseType)
    return method(HttpMethod.Get) { handle(body) }
  }

  inline fun <reified TParam : Any, reified TReq : Any, reified TResp : Any> Route.notarizedPost(
    info: MethodInfo,
    noinline body: PipelineInterceptor<Unit, ApplicationCall>
  ): Route = notarizationPreFlight<TParam, TReq, TResp>() { requestType, responseType ->
    val path = calculatePath()
    openApiSpec.paths.getOrPut(path) { OpenApiSpecPathItem() }
    openApiSpec.paths[path]?.post = info.parseMethodInfo(HttpMethod.Post, requestType, responseType)
    return method(HttpMethod.Post) { handle(body) }
  }

  inline fun <reified TParam : Any, reified TReq : Any, reified TResp : Any> Route.notarizedPut(
    info: MethodInfo,
    noinline body: PipelineInterceptor<Unit, ApplicationCall>,
  ): Route = notarizationPreFlight<TParam, TReq, TResp>() { requestType, responseType ->
    val path = calculatePath()
    openApiSpec.paths.getOrPut(path) { OpenApiSpecPathItem() }
    openApiSpec.paths[path]?.put = info.parseMethodInfo(HttpMethod.Put, requestType, responseType)
    return method(HttpMethod.Put) { handle(body) }
  }

  inline fun <reified TParam : Any, reified TResp : Any> Route.notarizedDelete(
    info: MethodInfo,
    noinline body: PipelineInterceptor<Unit, ApplicationCall>
  ): Route = notarizationPreFlight<TParam, Unit, TResp> { requestType, responseType ->
    val path = calculatePath()
    openApiSpec.paths.getOrPut(path) { OpenApiSpecPathItem() }
    openApiSpec.paths[path]?.delete = info.parseMethodInfo(HttpMethod.Delete, requestType, responseType)
    return method(HttpMethod.Delete) { handle(body) }
  }

  // TODO here down is a mess, needs refactor once core functionality is in place
  fun MethodInfo.parseMethodInfo(
    method: HttpMethod,
    requestType: KType,
    responseType: KType
  ) = OpenApiSpecPathItemOperation(
    summary = this.summary,
    description = this.description,
    tags = this.tags,
    deprecated = this.deprecated,
    responses = responseType.toSpec(responseInfo)?.let { mapOf(it) },
    requestBody = if (method != HttpMethod.Get) requestType.toSpec(requestInfo) else null
  )

  @OptIn(ExperimentalStdlibApi::class)
  inline fun <reified TParam : Any, reified TReq : Any, reified TResp : Any> notarizationPreFlight(
    block: (KType, KType) -> Route
  ): Route {
    val responseKontent = generateKontent<TResp>()
    val requestKontent = generateKontent<TReq>()
    val paramKontent = generateParameterKontent<TParam>()
    openApiSpec.components.schemas.putAll(responseKontent)
    openApiSpec.components.schemas.putAll(requestKontent)
    openApiSpec.components.schemas.putAll(paramKontent)
    val requestType = typeOf<TReq>()
    val responseType = typeOf<TResp>()
    return block.invoke(requestType, responseType)
  }

  // TODO These two lookin' real similar 👀 Combine?
  private fun KType.toSpec(requestInfo: RequestInfo?): OpenApiSpecRequest? = when (this) {
    Unit::class -> null
    else -> when (requestInfo) {
      null -> null
      else -> OpenApiSpecRequest(
        description = requestInfo.description,
        content = requestInfo.mediaTypes.associateWith {
          val ref = getReferenceSlug()
          OpenApiSpecMediaType.Referenced(OpenApiSpecReferenceObject(ref))
        }
      )
    }
  }

  private fun KType.toSpec(responseInfo: ResponseInfo?): Pair<Int, OpenApiSpecResponse>? = when (this) {
    Unit::class -> null // TODO Maybe not though? could be unit but 200 🤔
    else -> when (responseInfo) {
      null -> null // TODO again probably revisit this
      else -> {
        val content = responseInfo.mediaTypes.associateWith {
          val ref = getReferenceSlug()
          OpenApiSpecMediaType.Referenced(OpenApiSpecReferenceObject(ref))
        }
        val specResponse = OpenApiSpecResponse(
          description = responseInfo.description,
          content = content.ifEmpty { null }
        )
        Pair(responseInfo.status, specResponse)
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
