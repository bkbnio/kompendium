package org.leafygreens.kompendium

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpMethod
import io.ktor.routing.Route
import io.ktor.routing.method
import io.ktor.util.pipeline.PipelineInterceptor
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import org.leafygreens.kompendium.annotations.KompendiumField
import org.leafygreens.kompendium.annotations.KompendiumRequest
import org.leafygreens.kompendium.annotations.KompendiumResponse
import org.leafygreens.kompendium.models.meta.MethodInfo
import org.leafygreens.kompendium.models.oas.ArraySchema
import org.leafygreens.kompendium.models.oas.FormatSchema
import org.leafygreens.kompendium.models.oas.ObjectSchema
import org.leafygreens.kompendium.models.oas.OpenApiSpec
import org.leafygreens.kompendium.models.oas.OpenApiSpecComponentSchema
import org.leafygreens.kompendium.models.oas.OpenApiSpecInfo
import org.leafygreens.kompendium.models.oas.OpenApiSpecMediaType
import org.leafygreens.kompendium.models.oas.OpenApiSpecPathItem
import org.leafygreens.kompendium.models.oas.OpenApiSpecPathItemOperation
import org.leafygreens.kompendium.models.oas.OpenApiSpecReferenceObject
import org.leafygreens.kompendium.models.oas.OpenApiSpecRequest
import org.leafygreens.kompendium.models.oas.OpenApiSpecResponse
import org.leafygreens.kompendium.models.oas.SimpleSchema
import org.leafygreens.kompendium.util.Helpers.calculatePath
import org.leafygreens.kompendium.util.Helpers.putPairIfAbsent

object Kompendium {

  const val COMPONENT_SLUG = "#/components/schemas"

  val openApiSpec = OpenApiSpec(
    info = OpenApiSpecInfo(),
    servers = mutableListOf(),
    paths = mutableMapOf()
  )

  inline fun <reified TParam : Any, reified TResp : Any> Route.notarizedGet(
    info: MethodInfo,
    noinline body: PipelineInterceptor<Unit, ApplicationCall>
  ): Route = generateComponentSchemas<TParam, Unit, TResp>() {
    val path = calculatePath()
    openApiSpec.paths.getOrPut(path) { OpenApiSpecPathItem() }
    openApiSpec.paths[path]?.get = info.parseMethodInfo<Unit, TResp>()
    return method(HttpMethod.Get) { handle(body) }
  }

  inline fun <reified TParam : Any, reified TReq : Any, reified TResp : Any> Route.notarizedPost(
    info: MethodInfo,
    noinline body: PipelineInterceptor<Unit, ApplicationCall>
  ): Route = generateComponentSchemas<TParam, TReq, TResp>() {
    val path = calculatePath()
    openApiSpec.paths.getOrPut(path) { OpenApiSpecPathItem() }
    openApiSpec.paths[path]?.post = info.parseMethodInfo<TReq, TResp>()
    return method(HttpMethod.Post) { handle(body) }
  }

  inline fun <reified TParam : Any, reified TReq : Any, reified TResp : Any> Route.notarizedPut(
    info: MethodInfo,
    noinline body: PipelineInterceptor<Unit, ApplicationCall>,
  ): Route = generateComponentSchemas<TParam, TReq, TResp>() {
    val path = calculatePath()
    openApiSpec.paths.getOrPut(path) { OpenApiSpecPathItem() }
    openApiSpec.paths[path]?.put = info.parseMethodInfo<TReq, TResp>()
    return method(HttpMethod.Put) { handle(body) }
  }

  inline fun <reified TReq, reified TResp> MethodInfo.parseMethodInfo() = OpenApiSpecPathItemOperation(
    summary = this.summary,
    description = this.description,
    tags = this.tags,
    deprecated = this.deprecated,
    responses = parseResponseAnnotation<TResp>()?.let { mapOf(it) },
    requestBody = parseRequestAnnotation<TReq>()
  )

  inline fun <reified TParam : Any, reified TReq : Any, reified TResp : Any> generateComponentSchemas(
    block: () -> Route
  ): Route {
    if (TResp::class != Unit::class) openApiSpec.components.schemas.putPairIfAbsent(objectSchemaPair(TResp::class))
    if (TReq::class != Unit::class) openApiSpec.components.schemas.putPairIfAbsent(objectSchemaPair(TReq::class))
//    openApiSpec.components.schemas.putPairIfAbsent(objectSchemaPair(TParam::class))
    return block.invoke()
  }

  inline fun <reified TReq> parseRequestAnnotation(): OpenApiSpecRequest? = when (TReq::class) {
    Unit::class -> null
    else -> {
      val anny = TReq::class.findAnnotation<KompendiumRequest>() ?: error("My way or the highway bub")
      OpenApiSpecRequest(
        description = anny.description,
        content = anny.mediaTypes.associate {
          val ref = OpenApiSpecReferenceObject("$COMPONENT_SLUG/${TReq::class.simpleName}")
          val mediaType = OpenApiSpecMediaType.Referenced(ref)
          Pair(it, mediaType)
        }
      )
    }
  }

  inline fun <reified TResp> parseResponseAnnotation(): Pair<Int, OpenApiSpecResponse>? = when (TResp::class) {
    Unit::class -> null
    else -> {
      val anny = TResp::class.findAnnotation<KompendiumResponse>() ?: error("My way or the highway bub")
      val specResponse = OpenApiSpecResponse(
        description = anny.description,
        content = anny.mediaTypes.associate {
          val ref = OpenApiSpecReferenceObject("$COMPONENT_SLUG/${TResp::class.simpleName}")
          val mediaType = OpenApiSpecMediaType.Referenced(ref)
          Pair(it, mediaType)
        }
      )
      Pair(anny.status, specResponse)
    }
  }

  // TODO Investigate a caching mechanism to reduce overhead... then just reference once created
  fun objectSchemaPair(clazz: KClass<*>): Pair<String, ObjectSchema> {
    val o = objectSchema(clazz)
    return Pair(clazz.simpleName!!, o)
  }

  private fun objectSchema(clazz: KClass<*>): ObjectSchema =
    ObjectSchema(properties = clazz.memberProperties.associate { prop ->
      val field = prop.javaField?.type?.kotlin
      val anny = prop.findAnnotation<KompendiumField>()
      val schema = when (field) {
        List::class -> listFieldSchema(prop)
        else -> fieldToSchema(field as KClass<*>)
      }

      val name = anny?.let {
        anny.name
      } ?: prop.name

      Pair(name, schema)
    })

  private fun listFieldSchema(prop: KProperty<*>): ArraySchema {
    val listType = ((prop.javaField?.genericType
      as ParameterizedType).actualTypeArguments.first()
      as Class<*>).kotlin
    return ArraySchema(fieldToSchema(listType))
  }

  private fun fieldToSchema(field: KClass<*>): OpenApiSpecComponentSchema = when (field) {
    Int::class -> FormatSchema("int32", "integer")
    Long::class -> FormatSchema("int64", "integer")
    Double::class -> FormatSchema("double", "number")
    Float::class -> FormatSchema("float", "number")
    String::class -> SimpleSchema("string")
    Boolean::class -> SimpleSchema("boolean")
    else -> objectSchema(field)
  }

}
