package org.leafygreens.kompendium

import io.ktor.http.HttpMethod
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import org.leafygreens.kompendium.annotations.CookieParam
import org.leafygreens.kompendium.annotations.HeaderParam
import org.leafygreens.kompendium.annotations.PathParam
import org.leafygreens.kompendium.annotations.QueryParam
import org.leafygreens.kompendium.models.meta.ErrorMap
import org.leafygreens.kompendium.models.meta.MethodInfo
import org.leafygreens.kompendium.models.meta.RequestInfo
import org.leafygreens.kompendium.models.meta.ResponseInfo
import org.leafygreens.kompendium.models.meta.SchemaMap
import org.leafygreens.kompendium.models.oas.ExampleWrapper
import org.leafygreens.kompendium.models.oas.OpenApiSpec
import org.leafygreens.kompendium.models.oas.OpenApiSpecInfo
import org.leafygreens.kompendium.models.oas.OpenApiSpecMediaType
import org.leafygreens.kompendium.models.oas.OpenApiSpecParameter
import org.leafygreens.kompendium.models.oas.OpenApiSpecPathItemOperation
import org.leafygreens.kompendium.models.oas.OpenApiSpecReferencable
import org.leafygreens.kompendium.models.oas.OpenApiSpecReferenceObject
import org.leafygreens.kompendium.models.oas.OpenApiSpecRequest
import org.leafygreens.kompendium.models.oas.OpenApiSpecResponse
import org.leafygreens.kompendium.models.oas.OpenApiSpecSchemaRef
import org.leafygreens.kompendium.path.CorePathCalculator
import org.leafygreens.kompendium.path.PathCalculator
import org.leafygreens.kompendium.util.Helpers
import org.leafygreens.kompendium.util.Helpers.getReferenceSlug

object Kompendium {

  var errorMap: ErrorMap = emptyMap()
  var cache: SchemaMap = emptyMap()

  var openApiSpec = OpenApiSpec(
    info = OpenApiSpecInfo(),
    servers = mutableListOf(),
    paths = mutableMapOf()
  )

  var pathCalculator: PathCalculator = CorePathCalculator()

  // TODO here down is a mess, needs refactor once core functionality is in place
  fun parseMethodInfo(
    info: MethodInfo<*, *>,
    paramType: KType,
    requestType: KType,
    responseType: KType
  ) = OpenApiSpecPathItemOperation(
    summary = info.summary,
    description = info.description,
    tags = info.tags,
    deprecated = info.deprecated,
    parameters = paramType.toParameterSpec(),
    responses = responseType.toResponseSpec(info.responseInfo)?.let { mapOf(it) }.let {
      when (it) {
        null -> {
          val throwables = parseThrowables(info.canThrow)
          when (throwables.isEmpty()) {
            true -> null
            false -> throwables
          }
        }
        else -> it.plus(parseThrowables(info.canThrow))
      }
    },
    requestBody = when (info) {
      is MethodInfo.PutInfo<*, *, *> -> requestType.toRequestSpec(info.requestInfo)
      is MethodInfo.PostInfo<*, *, *> -> requestType.toRequestSpec(info.requestInfo)
      else -> null
    },
    security = if (info.securitySchemes.isNotEmpty()) listOf(
      // TODO support scopes
      info.securitySchemes.associateWith { listOf() }
    ) else null
  )

  private fun parseThrowables(throwables: Set<KClass<*>>): Map<Int, OpenApiSpecReferencable> = throwables.mapNotNull {
    errorMap[it.createType()]
  }.toMap()

  fun <TResp> ResponseInfo<TResp>.parseErrorInfo(
    errorType: KType,
    responseType: KType
  ) {
    errorMap = errorMap.plus(errorType to responseType.toResponseSpec(this))
  }

  // TODO These two lookin' real similar 👀 Combine?
  private fun <TReq> KType.toRequestSpec(requestInfo: RequestInfo<TReq>?): OpenApiSpecRequest<TReq>? =
    when (requestInfo) {
      null -> null
      else -> {
        OpenApiSpecRequest(
          description = requestInfo.description,
          content = resolveContent(requestInfo.mediaTypes, requestInfo.examples) ?: mapOf()
        )
      }
    }

  private fun <TResp> KType.toResponseSpec(responseInfo: ResponseInfo<TResp>?): Pair<Int, OpenApiSpecResponse<TResp>>? =
    when (responseInfo) {
      null -> null // TODO again probably revisit this
      else -> {
        val specResponse = OpenApiSpecResponse(
          description = responseInfo.description,
          content = resolveContent(responseInfo.mediaTypes, responseInfo.examples)
        )
        Pair(responseInfo.status, specResponse)
      }
    }

  private fun <F> KType.resolveContent(
    mediaTypes: List<String>,
    examples: Map<String, F>
  ): Map<String, OpenApiSpecMediaType<F>>? {
    return if (this != Helpers.UNIT_TYPE && mediaTypes.isNotEmpty()) {
      mediaTypes.associateWith {
        val ref = getReferenceSlug()
        OpenApiSpecMediaType(
          schema = OpenApiSpecReferenceObject(ref),
          examples = examples.mapValues { (_, v) -> ExampleWrapper(v) }.ifEmpty { null }
        )
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

  fun resetSchema() {
    openApiSpec = OpenApiSpec(
      info = OpenApiSpecInfo(),
      servers = mutableListOf(),
      paths = mutableMapOf()
    )
    cache = emptyMap()
  }
}
