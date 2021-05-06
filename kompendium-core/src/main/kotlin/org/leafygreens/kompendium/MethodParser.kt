package org.leafygreens.kompendium

import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaField
import org.leafygreens.kompendium.annotations.KompendiumParam
import org.leafygreens.kompendium.models.meta.MethodInfo
import org.leafygreens.kompendium.models.meta.RequestInfo
import org.leafygreens.kompendium.models.meta.ResponseInfo
import org.leafygreens.kompendium.models.oas.ExampleWrapper
import org.leafygreens.kompendium.models.oas.OpenApiSpecMediaType
import org.leafygreens.kompendium.models.oas.OpenApiSpecParameter
import org.leafygreens.kompendium.models.oas.OpenApiSpecPathItemOperation
import org.leafygreens.kompendium.models.oas.OpenApiSpecReferencable
import org.leafygreens.kompendium.models.oas.OpenApiSpecReferenceObject
import org.leafygreens.kompendium.models.oas.OpenApiSpecRequest
import org.leafygreens.kompendium.models.oas.OpenApiSpecResponse
import org.leafygreens.kompendium.util.Helpers
import org.leafygreens.kompendium.util.Helpers.getReferenceSlug
import org.leafygreens.kompendium.util.Helpers.getSimpleSlug

object MethodParser {
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
    Kompendium.errorMap[it.createType()]
  }.toMap()

  fun <TResp> ResponseInfo<TResp>.parseErrorInfo(
    errorType: KType,
    responseType: KType
  ) {
    Kompendium.errorMap = Kompendium.errorMap.plus(errorType to responseType.toResponseSpec(this))
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

  private fun KType.toParameterSpec(): List<OpenApiSpecParameter> {
    val clazz = classifier as KClass<*>
    return clazz.memberProperties.map { prop ->
      val field = prop.javaField?.type?.kotlin
        ?: error("Unable to parse field type from $prop")
      val anny = prop.findAnnotation<KompendiumParam>()
        ?: error("Field ${prop.name} is not annotated with KompendiumParam")
      val schema = Kompendium.cache[field.getSimpleSlug(prop)]
        ?: error("Could not find component type for $prop")
      val defaultValue = getDefaultParameterValue(clazz, prop)
      OpenApiSpecParameter(
        name = prop.name,
        `in` = anny.type.name.toLowerCase(),
        schema = schema.addDefault(defaultValue),
        description = anny.description.ifBlank { null },
        required = !prop.returnType.isMarkedNullable
      )
    }
  }

  private fun getDefaultParameterValue(clazz: KClass<*>, prop: KProperty<*>): Any? {
    val constructor = clazz.primaryConstructor
    val parameterInQuestion = constructor
      ?.parameters
      ?.find { it.name == prop.name }
      ?: error("could not find parameter ${prop.name}")
    if (!parameterInQuestion.isOptional) {
      return null
    }
    val values = constructor
      .parameters
      .filterNot { it.isOptional }
      .associateWith { defaultValueInjector(it) }
    val instance = constructor.callBy(values)
    val methods = clazz.java.methods
    val getterName = "get${prop.name.capitalize()}"
    val getterFunction = methods.find { it.name == getterName }
      ?: error("Could not associate ${prop.name} with a getter")
    return getterFunction.invoke(instance)
  }

  private fun defaultValueInjector(param: KParameter): Any = when (param.type.classifier) {
    String::class -> "test"
    Boolean::class -> false
    Int::class -> 1
    else -> error("Unsupported Type")
  }

}
