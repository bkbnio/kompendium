package io.bkbn.kompendium.core

import io.bkbn.kompendium.core.Kompendium.errorMap
import io.bkbn.kompendium.annotations.KompendiumParam
import io.bkbn.kompendium.core.metadata.MethodInfo
import io.bkbn.kompendium.core.metadata.RequestInfo
import io.bkbn.kompendium.core.metadata.ResponseInfo
import io.bkbn.kompendium.core.util.Helpers
import io.bkbn.kompendium.core.util.Helpers.getSimpleSlug
import io.bkbn.kompendium.oas.path.PathOperation
import io.bkbn.kompendium.oas.payload.MediaType
import io.bkbn.kompendium.oas.payload.Parameter
import io.bkbn.kompendium.oas.payload.Payload
import io.bkbn.kompendium.oas.payload.Request
import io.bkbn.kompendium.oas.payload.Response
import io.bkbn.kompendium.oas.schema.AnyOfSchema
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaField
import java.util.*

/**
 * The MethodParser is responsible for converting route metadata and types into an OpenAPI compatible data class.
 */
object MethodParser {

  /**
   * Generates the OpenAPI Path spec from provided metadata
   * @param info implementation of the [MethodInfo] sealed class
   * @param paramType Type of `TParam`
   * @param requestType Type of `TReq` if required
   * @param responseType Type of `TResp`
   * @return object representing the OpenAPI Path spec.
   */
  fun parseMethodInfo(
    info: MethodInfo<*, *>,
    paramType: KType,
    requestType: KType,
    responseType: KType
  ) = PathOperation (
    summary = info.summary,
    description = info.description,
    operationId = info.operationId,
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

  /**
   * Adds the error to the [Kompendium.errorMap] for reference in notarized routes.
   * @param errorType [KType] of the throwable being handled
   * @param responseType [KType] the type of the response sent in event of error
   */
  fun ResponseInfo<*>.parseErrorInfo(
    errorType: KType,
    responseType: KType
  ) {
    errorMap = errorMap.plus(errorType to responseType.toResponseSpec(this))
  }

  /**
   * Parses possible errors thrown by a route
   * @param throwables Set of classes that can be thrown
   * @return Mapping of status codes to their corresponding error spec
   */
  private fun parseThrowables(throwables: Set<KClass<*>>): Map<Int, Payload> = throwables.mapNotNull {
    errorMap[it.createType()]
  }.toMap()

  /**
   * Converts a [KType] to an [Request]
   * @receiver [KType] to convert
   * @param requestInfo request metadata
   * @return Will return a generated [Request] if requestInfo is not null
   */
  private fun KType.toRequestSpec(requestInfo: RequestInfo<*>?): Request<*>? =
    when (requestInfo) {
      null -> null
      else -> {
        Request(
          description = requestInfo.description,
          content = resolveContent(this, requestInfo.mediaTypes, requestInfo.examples) ?: mapOf()
        )
      }
    }

  /**
   * Converts a [KType] to a pairing of http status code to [Response]
   * @receiver [KType] to convert
   * @param responseInfo response metadata
   * @return Will return a generated [Pair] if responseInfo is not null
   */
  private fun KType.toResponseSpec(responseInfo: ResponseInfo<*>?): Pair<Int, Response<*>>? =
    when (responseInfo) {
      null -> null
      else -> {
        val specResponse = Response(
          description = responseInfo.description,
          content = resolveContent(this, responseInfo.mediaTypes, responseInfo.examples)
        )
        Pair(responseInfo.status.value, specResponse)
      }
    }

  /**
   * Generates MediaTypes along with any examples provided
   * @param type [KType] Type of the object
   * @param mediaTypes list of acceptable http media types
   * @param examples Mapping of named examples of valid bodies.
   * @return Named mapping of media types.
   */
  private fun <F> resolveContent(
    type: KType,
    mediaTypes: List<String>,
    examples: Map<String, F>
  ): Map<String, MediaType<F>>? {
    val classifier = type.classifier as KClass<*>
    return if (type != Helpers.UNIT_TYPE && mediaTypes.isNotEmpty()) {
      mediaTypes.associateWith {
        val schema = if (classifier.isSealed) {
          val refs = classifier.sealedSubclasses
            .map { it.createType(type.arguments) }
            .map { it.getSimpleSlug() }
            .map { Kompendium.cache[it] ?: error("$it not available") }
          AnyOfSchema(refs)
        } else {
          val ref = type.getSimpleSlug()
          Kompendium.cache[ref] ?: error("$ref not available")
        }
        MediaType(
          schema = schema,
          examples = examples.mapValues { (_, v) -> MediaType.Example(v) }.ifEmpty { null }
        )
      }
    } else null
  }

  /**
   * Parses a type for all parameter information.  All fields in the receiver
   * must be annotated with [io.bkbn.kompendium.annotations.KompendiumParam].
   * @receiver type
   * @return list of valid parameter specs as detailed by the [KType] members
   * @throws [IllegalStateException] if the class could not be parsed properly
   */
  private fun KType.toParameterSpec(): List<Parameter> {
    val clazz = classifier as KClass<*>
    return clazz.memberProperties.filter { prop ->
      prop.findAnnotation<KompendiumParam>() != null
    }.map { prop ->
      val field = prop.javaField?.type?.kotlin
        ?: error("Unable to parse field type from $prop")
      val anny = prop.findAnnotation<KompendiumParam>()
        ?: error("Field ${prop.name} is not annotated with KompendiumParam")
      val schema = Kompendium.cache[field.getSimpleSlug(prop)]
        ?: error("Could not find component type for $prop")
      val defaultValue = getDefaultParameterValue(clazz, prop)
      Parameter(
        name = prop.name,
        `in` = anny.type.name.lowercase(Locale.getDefault()),
        schema = schema.addDefault(defaultValue),
        description = anny.description.ifBlank { null },
        required = !prop.returnType.isMarkedNullable
      )
    }
  }

  /**
   * Absolutely disgusting reflection to determine if a default value is available for a given property.
   * @param clazz to which the property belongs
   * @param prop the property in question
   * @return The default value if found
   */
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

  /**
   * Allows the reflection invoker to populate a parameter map with values in order to sus out any default parameters.
   * @param param Parameter to provide value for
   * @return value of the proper type to match param
   * @throws [IllegalStateException] if parameter type is not one of the basic types supported below.
   */
  private fun defaultValueInjector(param: KParameter): Any = when (param.type.classifier) {
    String::class -> "test"
    Boolean::class -> false
    Int::class -> 1
    Long::class -> 2
    Double::class -> 1.0
    Float::class -> 1.0
    UUID::class -> UUID.randomUUID()
    else -> error("Unsupported Type")
  }
}