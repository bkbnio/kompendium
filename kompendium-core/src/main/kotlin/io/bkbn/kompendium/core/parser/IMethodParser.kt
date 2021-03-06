package io.bkbn.kompendium.core.parser

import io.bkbn.kompendium.annotations.Param
import io.bkbn.kompendium.core.Kompendium
import io.bkbn.kompendium.core.KompendiumPreFlight.generateReferences
import io.bkbn.kompendium.core.Kontent
import io.bkbn.kompendium.core.metadata.ExceptionInfo
import io.bkbn.kompendium.core.metadata.ParameterExample
import io.bkbn.kompendium.core.metadata.RequestInfo
import io.bkbn.kompendium.core.metadata.ResponseInfo
import io.bkbn.kompendium.core.metadata.method.MethodInfo
import io.bkbn.kompendium.core.metadata.method.PatchInfo
import io.bkbn.kompendium.core.metadata.method.PostInfo
import io.bkbn.kompendium.core.metadata.method.PutInfo
import io.bkbn.kompendium.core.util.Helpers
import io.bkbn.kompendium.core.util.Helpers.capitalized
import io.bkbn.kompendium.core.util.Helpers.getReferenceSlug
import io.bkbn.kompendium.core.util.Helpers.getSimpleSlug
import io.bkbn.kompendium.oas.path.PathOperation
import io.bkbn.kompendium.oas.payload.MediaType
import io.bkbn.kompendium.oas.payload.Parameter
import io.bkbn.kompendium.oas.payload.Request
import io.bkbn.kompendium.oas.payload.Response
import io.bkbn.kompendium.oas.schema.AnyOfSchema
import io.bkbn.kompendium.oas.schema.ObjectSchema
import io.bkbn.kompendium.oas.schema.ReferencedSchema
import io.ktor.routing.Route
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import java.util.Locale
import java.util.UUID

interface IMethodParser {
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
    responseType: KType,
    feature: Kompendium
  ) = PathOperation(
    summary = info.summary,
    description = info.description,
    operationId = info.operationId,
    tags = info.tags,
    deprecated = info.deprecated,
    parameters = paramType.toParameterSpec(info, feature),
    responses = parseResponse(responseType, info.responseInfo, feature).plus(parseExceptions(info.canThrow, feature)),
    requestBody = when (info) {
      is PutInfo<*, *, *> -> requestType.toRequestSpec(info.requestInfo, feature)
      is PostInfo<*, *, *> -> requestType.toRequestSpec(info.requestInfo, feature)
      is PatchInfo<*, *, *> -> requestType.toRequestSpec(info.requestInfo, feature)
      else -> null
    },
    security = if (info.securitySchemes.isNotEmpty()) listOf(
      // TODO support scopes
      info.securitySchemes.associateWith { listOf() }
    ) else null
  )

  fun parseResponse(
    responseType: KType,
    responseInfo: ResponseInfo<*>?,
    feature: Kompendium
  ): Map<Int, Response> = responseType.toResponseSpec(responseInfo, feature)?.let { mapOf(it) }.orEmpty()

  fun parseExceptions(
    exceptionInfo: Set<ExceptionInfo<*>>,
    feature: Kompendium,
  ): Map<Int, Response> = exceptionInfo.associate { info ->
    Kontent.generateKontent(info.responseType, feature.config.bodyCache)
    feature.generateReferences()
    val response = Response(
      description = info.description,
      content = feature.resolveContent(info.responseType, info.mediaTypes, info.examples)
    )
    Pair(info.status.value, response)
  }

  /**
   * Converts a [KType] to an [Request]
   * @receiver [KType] to convert
   * @param requestInfo request metadata
   * @return Will return a generated [Request] if requestInfo is not null
   */
  fun KType.toRequestSpec(requestInfo: RequestInfo<*>?, feature: Kompendium): Request? =
    when (requestInfo) {
      null -> null
      else -> {
        Request(
          description = requestInfo.description,
          content = feature.resolveContent(this, requestInfo.mediaTypes, requestInfo.examples as Map<String, Any>)
            ?: mapOf(),
          required = requestInfo.required
        )
      }
    }

  /**
   * Converts a [KType] to a pairing of http status code to [Response]
   * @receiver [KType] to convert
   * @param responseInfo response metadata
   * @return Will return a generated [Pair] if responseInfo is not null
   */
  fun KType.toResponseSpec(responseInfo: ResponseInfo<*>?, feature: Kompendium): Pair<Int, Response>? =
    when (responseInfo) {
      null -> null
      else -> {
        val specResponse = Response(
          description = responseInfo.description,
          content = feature.resolveContent(this, responseInfo.mediaTypes, responseInfo.examples as Map<String, Any>)
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
  fun Kompendium.resolveContent(
    type: KType,
    mediaTypes: List<String>,
    examples: Map<String, Any>
  ): Map<String, MediaType>? {
    val classifier = type.classifier as KClass<*>
    return if (type != Helpers.UNIT_TYPE && mediaTypes.isNotEmpty()) {
      mediaTypes.associateWith {
        val schema = if (classifier.isSealed) {
          val refs = classifier.sealedSubclasses
            .map { it.createType(type.arguments) }
            .map { ReferencedSchema(it.getReferenceSlug()) }
          AnyOfSchema(refs)
        } else {
          if (config.spec.components.schemas.containsKey(type.getSimpleSlug())) {
            ReferencedSchema(type.getReferenceSlug())
          } else {
            config.bodyCache[type.getSimpleSlug()] ?: error("REEEE")
          }
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
   * must be annotated with [io.bkbn.kompendium.annotations.Param].
   * @receiver type
   * @return list of valid parameter specs as detailed by the [KType] members
   * @throws [IllegalStateException] if the class could not be parsed properly
   */
  fun KType.toParameterSpec(info: MethodInfo<*, *>, feature: Kompendium): List<Parameter> {
    val clazz = classifier as KClass<*>
    return clazz.memberProperties
      .filter { prop -> prop.hasAnnotation<Param>() }
      .map { prop -> prop.toParameter(info, this, clazz, feature) }
  }

  fun KProperty<*>.toParameter(
    info: MethodInfo<*, *>,
    parentType: KType,
    parentClazz: KClass<*>,
    feature: Kompendium
  ): Parameter {
    val wrapperSchema = feature.config.parameterCache[parentType.getSimpleSlug()]!! as ObjectSchema
    val anny = this.findAnnotation<Param>()
      ?: error("Field $name is not annotated with KompendiumParam")
    val schema = wrapperSchema.properties[name]
      ?: error("Could not find component type for $this")
    val defaultValue = getDefaultParameterValue(parentClazz, this)
    return Parameter(
      name = name,
      `in` = anny.type.name.lowercase(Locale.getDefault()),
      schema = schema.addDefault(defaultValue),
      description = schema.description,
      required = !returnType.isMarkedNullable && defaultValue == null,
      examples = info.parameterExamples.mapToSpec(name)
    )
  }

  fun Set<ParameterExample>.mapToSpec(parameterName: String): Map<String, Parameter.Example>? {
    val filtered = filter { it.parameterName == parameterName }
    return if (filtered.isEmpty()) {
      null
    } else {
      filtered.associate { it.exampleName to Parameter.Example(it.exampleValue) }
    }
  }

  /**
   * Absolutely disgusting reflection to determine if a default value is available for a given property.
   * @param clazz to which the property belongs
   * @param prop the property in question
   * @return The default value if found
   */
  fun getDefaultParameterValue(clazz: KClass<*>, prop: KProperty<*>): Any? {
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
    val getterName = "get${prop.name.capitalized()}"
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
  fun defaultValueInjector(param: KParameter): Any = when (param.type.classifier) {
    String::class -> "test"
    Boolean::class -> false
    Int::class -> 1
    Long::class -> 2
    Double::class -> 1.0
    Float::class -> 1.0
    UUID::class -> UUID.randomUUID()
    else -> error("Unsupported Type")
  }

  /**
   * Uses the built-in Ktor route path [Route.toString] but cuts out any meta route such as authentication... anything
   * that matches the RegEx pattern `/\\(.+\\)`
   */
  fun Route.calculateRoutePath() = toString().replace(Regex("/\\(.+\\)"), "")
}
