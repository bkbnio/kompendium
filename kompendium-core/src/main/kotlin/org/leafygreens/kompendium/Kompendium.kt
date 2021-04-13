package org.leafygreens.kompendium

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpMethod
import io.ktor.routing.Route
import io.ktor.routing.createRouteFromPath
import io.ktor.routing.method
import io.ktor.util.pipeline.PipelineInterceptor
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import org.leafygreens.kompendium.annotations.KompendiumField
import org.leafygreens.kompendium.annotations.KompendiumInternal
import org.leafygreens.kompendium.models.oas.ArraySchema
import org.leafygreens.kompendium.models.oas.FormatSchema
import org.leafygreens.kompendium.models.oas.ObjectSchema
import org.leafygreens.kompendium.models.oas.OpenApiSpec
import org.leafygreens.kompendium.models.oas.OpenApiSpecComponentSchema
import org.leafygreens.kompendium.models.oas.OpenApiSpecInfo
import org.leafygreens.kompendium.models.oas.OpenApiSpecPathItem
import org.leafygreens.kompendium.models.oas.OpenApiSpecPathItemOperation
import org.leafygreens.kompendium.models.oas.SimpleSchema
import org.leafygreens.kompendium.models.meta.MethodInfo
import org.leafygreens.kompendium.util.Helpers.calculatePath
import org.leafygreens.kompendium.util.Helpers.putPairIfAbsent

object Kompendium {
  val openApiSpec = OpenApiSpec(
    info = OpenApiSpecInfo(),
    servers = mutableListOf(),
    paths = mutableMapOf()
  )

  fun Route.notarizedGet(info: MethodInfo, body: PipelineInterceptor<Unit, ApplicationCall>): Route {
    val path = calculatePath()
    openApiSpec.paths.getOrPut(path) { OpenApiSpecPathItem() }
    openApiSpec.paths[path]?.get = OpenApiSpecPathItemOperation(
      summary = info.summary,
      description = info.description,
      tags = info.tags
    )
    return method(HttpMethod.Get) { handle(body) }
  }

  inline fun <reified TQ : Any, reified TP : Any, reified TR : Any> Route.notarizedPost(
    info: MethodInfo,
    noinline body: PipelineInterceptor<Unit, ApplicationCall>
  ): Route = generateComponentSchemas<TQ, TP, TR>(info, body) { i, b ->
    val path = calculatePath()
    openApiSpec.paths.getOrPut(path) { OpenApiSpecPathItem() }
    openApiSpec.paths[path]?.post = OpenApiSpecPathItemOperation(
      summary = i.summary,
      description = i.description,
      tags = i.tags
    )
    return method(HttpMethod.Post) { handle(b) }
  }

  inline fun <reified TQ : Any, reified TP : Any, reified TR : Any> Route.notarizedPut(
    info: MethodInfo,
    noinline body: PipelineInterceptor<Unit, ApplicationCall>,
  ): Route = generateComponentSchemas<TQ, TP, TR>(info, body) { i, b ->
    val path = calculatePath()
    openApiSpec.paths.getOrPut(path) { OpenApiSpecPathItem() }
    openApiSpec.paths[path]?.put = OpenApiSpecPathItemOperation(
      summary = i.summary,
      description = i.description,
      tags = i.tags
    )
    return method(HttpMethod.Put) { handle(b) }
  }

  @OptIn(KompendiumInternal::class)
  inline fun <reified TQ : Any, reified TP : Any, reified TR : Any> generateComponentSchemas(
    info: MethodInfo,
    noinline body: PipelineInterceptor<Unit, ApplicationCall>,
    block: (MethodInfo, PipelineInterceptor<Unit, ApplicationCall>) -> Route
  ): Route {
    openApiSpec.components.schemas.putPairIfAbsent(objectSchemaPair(TQ::class))
    openApiSpec.components.schemas.putPairIfAbsent(objectSchemaPair(TR::class))
    openApiSpec.components.schemas.putPairIfAbsent(objectSchemaPair(TP::class))
    return block.invoke(info, body)
  }

  @KompendiumInternal
  // TODO Investigate a caching mechanism to reduce overhead... then just reference once created
  fun objectSchemaPair(clazz: KClass<*>): Pair<String, ObjectSchema> {
    val o = objectSchema(clazz)
    return Pair(clazz.qualifiedName!!, o)
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

  @OptIn(KompendiumInternal::class)
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
