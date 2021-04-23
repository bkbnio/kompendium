package org.leafygreens.kompendium

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpMethod
import io.ktor.routing.Route
import io.ktor.routing.method
import io.ktor.util.pipeline.PipelineInterceptor
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import kotlin.reflect.typeOf
import org.leafygreens.kompendium.Kontent.generateKontent
import org.leafygreens.kompendium.Kontent.generateParameterKontent
import org.leafygreens.kompendium.annotations.CookieParam
import org.leafygreens.kompendium.annotations.HeaderParam
import org.leafygreens.kompendium.annotations.PathParam
import org.leafygreens.kompendium.annotations.QueryParam
import org.leafygreens.kompendium.models.meta.MethodInfo
import org.leafygreens.kompendium.models.meta.RequestInfo
import org.leafygreens.kompendium.models.meta.ResponseInfo
import org.leafygreens.kompendium.models.meta.SchemaMap
import org.leafygreens.kompendium.models.oas.OpenApiSpec
import org.leafygreens.kompendium.models.oas.OpenApiSpecInfo
import org.leafygreens.kompendium.models.oas.OpenApiSpecMediaType
import org.leafygreens.kompendium.models.oas.OpenApiSpecParameter
import org.leafygreens.kompendium.models.oas.OpenApiSpecPathItem
import org.leafygreens.kompendium.models.oas.OpenApiSpecPathItemOperation
import org.leafygreens.kompendium.models.oas.OpenApiSpecReferenceObject
import org.leafygreens.kompendium.models.oas.OpenApiSpecRequest
import org.leafygreens.kompendium.models.oas.OpenApiSpecResponse
import org.leafygreens.kompendium.models.oas.OpenApiSpecSchemaRef
import org.leafygreens.kompendium.path.CorePathCalculator
import org.leafygreens.kompendium.path.PathCalculator
import org.leafygreens.kompendium.util.Helpers
import org.leafygreens.kompendium.util.Helpers.getReferenceSlug

object Kompendium {

  var cache: SchemaMap = emptyMap()

  var openApiSpec = OpenApiSpec(
    info = OpenApiSpecInfo(),
    servers = mutableListOf(),
    paths = mutableMapOf()
  )

  var pathCalculator: PathCalculator = CorePathCalculator()

  @OptIn(ExperimentalStdlibApi::class)
  inline fun <reified TParam : Any, reified TResp : Any> Route.notarizedGet(
    info: MethodInfo,
    noinline body: PipelineInterceptor<Unit, ApplicationCall>
  ): Route = notarizationPreFlight<TParam, Unit, TResp>() { paramType, requestType, responseType ->
    val path = pathCalculator.calculate(this)
    openApiSpec.paths.getOrPut(path) { OpenApiSpecPathItem() }
    openApiSpec.paths[path]?.get = info.parseMethodInfo(HttpMethod.Get, paramType, requestType, responseType)
    return method(HttpMethod.Get) { handle(body) }
  }

  inline fun <reified TParam : Any, reified TReq : Any, reified TResp : Any> Route.notarizedPost(
    info: MethodInfo,
    noinline body: PipelineInterceptor<Unit, ApplicationCall>
  ): Route = notarizationPreFlight<TParam, TReq, TResp>() { paramType, requestType, responseType ->
    val path = pathCalculator.calculate(this)
    openApiSpec.paths.getOrPut(path) { OpenApiSpecPathItem() }
    openApiSpec.paths[path]?.post = info.parseMethodInfo(HttpMethod.Post, paramType, requestType, responseType)
    return method(HttpMethod.Post) { handle(body) }
  }

  inline fun <reified TParam : Any, reified TReq : Any, reified TResp : Any> Route.notarizedPut(
    info: MethodInfo,
    noinline body: PipelineInterceptor<Unit, ApplicationCall>,
  ): Route = notarizationPreFlight<TParam, TReq, TResp>() { paramType, requestType, responseType ->
    val path = pathCalculator.calculate(this)
    openApiSpec.paths.getOrPut(path) { OpenApiSpecPathItem() }
    openApiSpec.paths[path]?.put = info.parseMethodInfo(HttpMethod.Put, paramType, requestType, responseType)
    return method(HttpMethod.Put) { handle(body) }
  }

  inline fun <reified TParam : Any, reified TResp : Any> Route.notarizedDelete(
    info: MethodInfo,
    noinline body: PipelineInterceptor<Unit, ApplicationCall>
  ): Route = notarizationPreFlight<TParam, Unit, TResp> { paramType, requestType, responseType ->
    val path = pathCalculator.calculate(this)
    openApiSpec.paths.getOrPut(path) { OpenApiSpecPathItem() }
    openApiSpec.paths[path]?.delete = info.parseMethodInfo(HttpMethod.Delete, paramType, requestType, responseType)
    return method(HttpMethod.Delete) { handle(body) }
  }

  // TODO here down is a mess, needs refactor once core functionality is in place
  fun MethodInfo.parseMethodInfo(
    method: HttpMethod,
    paramType: KType,
    requestType: KType,
    responseType: KType
  ) = OpenApiSpecPathItemOperation(
    summary = this.summary,
    description = this.description,
    tags = this.tags,
    deprecated = this.deprecated,
    parameters = paramType.toParameterSpec(),
    responses = responseType.toResponseSpec(responseInfo)?.let { mapOf(it) },
    requestBody = if (method != HttpMethod.Get) requestType.toRequestSpec(requestInfo) else null,
    security = if (this.securitySchemes.isNotEmpty()) listOf(
      // TODO support scopes
      this.securitySchemes.associateWith { listOf() }
    ) else null
  )

  @OptIn(ExperimentalStdlibApi::class)
  inline fun <reified TParam : Any, reified TReq : Any, reified TResp : Any> notarizationPreFlight(
    block: (KType, KType, KType) -> Route
  ): Route {
    cache = generateKontent<TResp>(cache)
    cache = generateKontent<TReq>(cache)
    cache = generateParameterKontent<TParam>(cache)
    openApiSpec.components.schemas.putAll(cache)
    val requestType = typeOf<TReq>()
    val responseType = typeOf<TResp>()
    val paramType = typeOf<TParam>()
    return block.invoke(paramType, requestType, responseType)
  }

  // TODO These two lookin' real similar 👀 Combine?
  private fun KType.toRequestSpec(requestInfo: RequestInfo?): OpenApiSpecRequest? = when (requestInfo) {
    null -> null
    else -> {
      OpenApiSpecRequest(
        description = requestInfo.description,
        content = resolveContent(requestInfo.mediaTypes) ?: mapOf()
      )
    }
  }

  private fun KType.toResponseSpec(responseInfo: ResponseInfo?): Pair<Int, OpenApiSpecResponse>? = when (responseInfo) {
    null -> null // TODO again probably revisit this
    else -> {
      val specResponse = OpenApiSpecResponse(
        description = responseInfo.description,
        content = resolveContent(responseInfo.mediaTypes)
      )
      Pair(responseInfo.status, specResponse)
    }
  }

  private fun KType.resolveContent(mediaTypes: List<String>): Map<String, OpenApiSpecMediaType>? {
    return if (this != Helpers.UNIT_TYPE && mediaTypes.isNotEmpty()) {
      mediaTypes.associateWith {
        val ref = getReferenceSlug()
        OpenApiSpecMediaType.Referenced(OpenApiSpecReferenceObject(ref))
      }
    } else null
  }


  // TODO God these annotations make this hideous... any way to improve?
  private fun KType.toParameterSpec(): List<OpenApiSpecParameter> {
    val clazz = classifier as KClass<*>
    return clazz.memberProperties.map { prop ->
      val field = prop.javaField?.type?.kotlin
        ?: error("Unable to parse field type from $prop")
      val anny = prop.findAnnotation<PathParam>()
        ?: prop.findAnnotation<QueryParam>()
        ?: prop.findAnnotation<HeaderParam>()
        ?: prop.findAnnotation<CookieParam>()
        ?: error("Unable to find any relevant parameter specifier annotations on field ${prop.name}")
      OpenApiSpecParameter(
        name = prop.name,
        `in` = when (anny) {
          is PathParam -> "path"
          is QueryParam -> "query"
          is HeaderParam -> "header"
          is CookieParam -> "cookie"
          else -> error("should not be reachable")
        },
        schema = OpenApiSpecSchemaRef(field.getReferenceSlug(prop)),
        description = when (anny) {
          is PathParam -> anny.description.ifBlank { null }
          is QueryParam -> anny.description.ifBlank { null }
          is HeaderParam -> anny.description.ifBlank { null }
          is CookieParam -> anny.description.ifBlank { null }
          else -> error("should not be reachable")
        },
        required = !prop.returnType.isMarkedNullable
      )
    }
  }

  internal fun resetSchema() {
    openApiSpec = OpenApiSpec(
      info = OpenApiSpecInfo(),
      servers = mutableListOf(),
      paths = mutableMapOf()
    )
    cache = emptyMap()
  }
}
