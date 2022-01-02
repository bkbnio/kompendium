package io.bkbn.kompendium.core

import io.bkbn.kompendium.annotations.Field
import io.bkbn.kompendium.annotations.UndeclaredField
import io.bkbn.kompendium.annotations.constraint.ExclusiveMaximum
import io.bkbn.kompendium.annotations.constraint.ExclusiveMinimum
import io.bkbn.kompendium.annotations.constraint.Format
import io.bkbn.kompendium.annotations.FreeFormObject
import io.bkbn.kompendium.annotations.constraint.MaxItems
import io.bkbn.kompendium.annotations.constraint.MaxLength
import io.bkbn.kompendium.annotations.constraint.MaxProperties
import io.bkbn.kompendium.annotations.constraint.Maximum
import io.bkbn.kompendium.annotations.constraint.MinItems
import io.bkbn.kompendium.annotations.constraint.MinLength
import io.bkbn.kompendium.annotations.constraint.MinProperties
import io.bkbn.kompendium.annotations.constraint.Minimum
import io.bkbn.kompendium.annotations.constraint.MultipleOf
import io.bkbn.kompendium.annotations.constraint.Pattern
import io.bkbn.kompendium.annotations.constraint.UniqueItems
import io.bkbn.kompendium.core.metadata.SchemaMap
import io.bkbn.kompendium.core.metadata.TypeMap
import io.bkbn.kompendium.core.util.Helpers.genericNameAdapter
import io.bkbn.kompendium.core.util.Helpers.getSimpleSlug
import io.bkbn.kompendium.core.util.Helpers.logged
import io.bkbn.kompendium.oas.schema.AnyOfSchema
import io.bkbn.kompendium.oas.schema.ArraySchema
import io.bkbn.kompendium.oas.schema.ComponentSchema
import io.bkbn.kompendium.oas.schema.DictionarySchema
import io.bkbn.kompendium.oas.schema.EnumSchema
import io.bkbn.kompendium.oas.schema.FormattedSchema
import io.bkbn.kompendium.oas.schema.FreeFormSchema
import io.bkbn.kompendium.oas.schema.ObjectSchema
import io.bkbn.kompendium.oas.schema.SimpleSchema
import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaField
import kotlin.reflect.typeOf
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.math.BigInteger
import java.util.UUID

/**
 * Responsible for generating the schema map that is used to power all object references across the API Spec.
 */
object Kontent {

  private val logger = LoggerFactory.getLogger(javaClass)

  /**
   * Analyzes a type [T] for its top-level and any nested schemas, and adds them to a [SchemaMap], if provided
   * @param T type to analyze
   * @param cache Existing schema map to append to
   * @return an updated schema map containing all type information for [T]
   */
  @OptIn(ExperimentalStdlibApi::class)
  inline fun <reified T> generateKontent(
    cache: SchemaMap = emptyMap()
  ): SchemaMap {
    val kontentType = typeOf<T>()
    return generateKTypeKontent(kontentType, cache)
  }

  /**
   * Analyzes a [KType] for its top-level and any nested schemas, and adds them to a [SchemaMap], if provided
   * @param type [KType] to analyze
   * @param cache Existing schema map to append to
   * @return an updated schema map containing all type information for [KType] type
   */
  fun generateKontent(
    type: KType,
    cache: SchemaMap = emptyMap()
  ): SchemaMap {
    var newCache = cache
    gatherSubTypes(type).forEach {
      newCache = generateKTypeKontent(it, newCache)
    }
    return newCache
  }

  private fun gatherSubTypes(type: KType): List<KType> {
    val classifier = type.classifier as KClass<*>
    return if (classifier.isSealed) {
      classifier.sealedSubclasses.map {
        it.createType(type.arguments)
      }
    } else {
      listOf(type)
    }
  }

  /**
   * Recursively fills schema map depending on [KType] classifier
   * @param type [KType] to parse
   * @param cache Existing schema map to append to
   */
  fun generateKTypeKontent(
    type: KType,
    cache: SchemaMap = emptyMap(),
  ): SchemaMap = logged(object {}.javaClass.enclosingMethod.name, mapOf("cache" to cache)) {
    logger.debug("Parsing Kontent of $type")
    when (val clazz = type.classifier as KClass<*>) {
      Unit::class -> cache
      Int::class -> cache.plus(clazz.simpleName!! to FormattedSchema("int32", "integer"))
      Long::class -> cache.plus(clazz.simpleName!! to FormattedSchema("int64", "integer"))
      Double::class -> cache.plus(clazz.simpleName!! to FormattedSchema("double", "number"))
      Float::class -> cache.plus(clazz.simpleName!! to FormattedSchema("float", "number"))
      String::class -> cache.plus(clazz.simpleName!! to SimpleSchema("string"))
      Boolean::class -> cache.plus(clazz.simpleName!! to SimpleSchema("boolean"))
      UUID::class -> cache.plus(clazz.simpleName!! to FormattedSchema("uuid", "string"))
      BigDecimal::class -> cache.plus(clazz.simpleName!! to FormattedSchema("double", "number"))
      BigInteger::class -> cache.plus(clazz.simpleName!! to FormattedSchema("int64", "integer"))
      ByteArray::class -> cache.plus(clazz.simpleName!! to FormattedSchema("byte", "string"))
      else -> when {
        clazz.isSubclassOf(Collection::class) -> handleCollectionType(type, clazz, cache)
        clazz.isSubclassOf(Enum::class) -> handleEnumType(clazz, cache)
        clazz.isSubclassOf(Map::class) -> handleMapType(type, clazz, cache)
        else -> handleComplexType(type, clazz, cache)
      }
    }
  }

  /**
   * In the event of an object type, this method will parse out individual fields to recursively aggregate object map.
   * @param clazz Class of the object to analyze
   * @param cache Existing schema map to append to
   */
  @Suppress("LongMethod", "ComplexMethod")
  private fun handleComplexType(type: KType, clazz: KClass<*>, cache: SchemaMap): SchemaMap {
    // This needs to be simple because it will be stored under its appropriate reference component implicitly
    val slug = type.getSimpleSlug()
    // Only analyze if component has not already been stored in the cache
    return when (cache.containsKey(slug)) {
      true -> {
        logger.debug("Cache already contains $slug, returning cache untouched")
        cache
      }
      false -> {
        logger.debug("$slug was not found in cache, generating now")
        var newCache = cache
        // Grabs any type parameters mapped to the corresponding type argument(s)
        val typeMap: TypeMap = clazz.typeParameters.zip(type.arguments).toMap()
        // associates each member with a Pair of prop name to property schema
        val fieldMap = clazz.memberProperties.associate { prop ->
          logger.debug("Analyzing $prop in class $clazz")
          // Grab the field of the current property
          val field = prop.javaField?.type?.kotlin ?: error("Unable to parse field type from $prop")
          // Short circuit if data is free form
          val freeForm = prop.findAnnotation<FreeFormObject>()
          var name = prop.name

          // todo add method to clean up
          when (freeForm) {
            null -> {
              val baseType = scanForGeneric(typeMap, prop)
              val baseClazz = baseType.classifier as KClass<*>
              val allTypes = scanForSealed(baseClazz, baseType)
              newCache = updateCache(newCache, field, allTypes)
              var propSchema = constructComponentSchema(
                typeMap = typeMap,
                prop = prop,
                fieldClazz = field,
                clazz = baseClazz,
                type = baseType,
                cache = newCache
              )
              // todo move to helper
              prop.findAnnotation<Field>()?.let { fieldOverrides ->
                if (fieldOverrides.description.isNotBlank()) {
                  propSchema = propSchema.setDescription(fieldOverrides.description)
                }
                if (fieldOverrides.name.isNotBlank()) {
                  name = fieldOverrides.name
                }
              }
              Pair(name, propSchema)
            }
            else -> {
              val minProperties = prop.findAnnotation<MinProperties>()
              val maxProperties = prop.findAnnotation<MaxProperties>()
              val schema =
                FreeFormSchema(minProperties = minProperties?.properties, maxProperties = maxProperties?.properties)
              Pair(name, schema)
            }
          }
        }
        logger.debug("Looking for undeclared fields")
        val undeclaredFieldMap = clazz.annotations.filterIsInstance<UndeclaredField>().associate {
          val undeclaredType = it.clazz.createType()
          newCache = generateKontent(undeclaredType, newCache)
          it.field to newCache[undeclaredType.getSimpleSlug()]!!
        }
        logger.debug("$slug contains $fieldMap")
        var schema = ObjectSchema(fieldMap.plus(undeclaredFieldMap))
        val requiredParams = clazz.primaryConstructor?.parameters?.filterNot { it.isOptional } ?: emptyList()
        if (requiredParams.isNotEmpty()) {
          schema = schema.copy(required = requiredParams.map { it.name!! })
        }
        logger.debug("$slug schema: $schema")
        newCache.plus(slug to schema)
      }
    }
  }

  /**
   * Takes the type information provided and adds any missing data to the schema map
   */
  private fun updateCache(cache: SchemaMap, clazz: KClass<*>, types: List<KType>): SchemaMap {
    var newCache = cache
    if (!cache.containsKey(clazz.simpleName)) {
      logger.debug("Cache was missing ${clazz.simpleName}, adding now")
      types.forEach {
        newCache = generateKTypeKontent(it, newCache)
      }
    }
    return newCache
  }

  /**
   * Scans a class for sealed subclasses.  If found, returns a list with all children.  Otherwise, returns
   * the base type
   */
  private fun scanForSealed(clazz: KClass<*>, type: KType): List<KType> = if (clazz.isSealed) {
    clazz.sealedSubclasses.map { it.createType(type.arguments) }
  } else {
    listOf(type)
  }

  /**
   * Yoinks any generic types from the type map should the field be a generic
   */
  private fun scanForGeneric(typeMap: TypeMap, prop: KProperty1<*, *>): KType =
    if (typeMap.containsKey(prop.returnType.classifier)) {
      logger.debug("Generic type detected")
      typeMap[prop.returnType.classifier]?.type!!
    } else {
      prop.returnType
    }

  /**
   * Constructs a [ComponentSchema]
   */
  private fun constructComponentSchema(
    typeMap: TypeMap,
    clazz: KClass<*>,
    fieldClazz: KClass<*>,
    prop: KProperty1<*, *>,
    type: KType,
    cache: SchemaMap
  ): ComponentSchema =
    when (typeMap.containsKey(prop.returnType.classifier)) {
      true -> handleGenericProperty(typeMap, clazz, type, prop.returnType.classifier, cache)
      false -> handleStandardProperty(clazz, fieldClazz, prop, type, cache)
    }.scanForConstraints(clazz, prop)

  private fun ComponentSchema.scanForConstraints(clazz: KClass<*>, prop: KProperty1<*, *>): ComponentSchema =
    when (this) {
      is AnyOfSchema -> AnyOfSchema(anyOf.map { it.scanForConstraints(clazz, prop) })
      is ArraySchema -> scanForConstraints(prop)
      is DictionarySchema -> this // TODO Anything here?
      is EnumSchema -> scanForConstraints(prop)
      is FormattedSchema -> scanForConstraints(prop)
      is FreeFormSchema -> this // todo anything here?
      is ObjectSchema -> scanForConstraints(clazz, prop)
      is SimpleSchema -> scanForConstraints(prop)
    }

  private fun ArraySchema.scanForConstraints(prop: KProperty1<*, *>): ArraySchema {
    val minItems = prop.findAnnotation<MinItems>()
    val maxItems = prop.findAnnotation<MaxItems>()
    val uniqueItems = prop.findAnnotation<UniqueItems>()

    return this.copy(
      minItems = minItems?.items,
      maxItems = maxItems?.items,
      uniqueItems = uniqueItems?.let { true }
    )
  }

  private fun EnumSchema.scanForConstraints(prop: KProperty1<*, *>): EnumSchema {
    if (prop.returnType.isMarkedNullable) {
      return this.copy(nullable = true)
    }

    return this
  }

  private fun FormattedSchema.scanForConstraints(prop: KProperty1<*, *>): FormattedSchema {
    val minimum = prop.findAnnotation<Minimum>()
    val maximum = prop.findAnnotation<Maximum>()
    val exclusiveMinimum = prop.findAnnotation<ExclusiveMinimum>()
    val exclusiveMaximum = prop.findAnnotation<ExclusiveMaximum>()
    val multipleOf = prop.findAnnotation<MultipleOf>()

    var schema = this

    if (prop.returnType.isMarkedNullable) {
      schema = schema.copy(nullable = true)
    }

    return schema.copy(
      minimum = minimum?.min,
      maximum = maximum?.max,
      exclusiveMinimum = exclusiveMinimum?.let { true },
      exclusiveMaximum = exclusiveMaximum?.let { true },
      multipleOf = multipleOf?.multiple,
    )
  }

  private fun SimpleSchema.scanForConstraints(prop: KProperty1<*, *>): SimpleSchema {
    val minLength = prop.findAnnotation<MinLength>()
    val maxLength = prop.findAnnotation<MaxLength>()
    val pattern = prop.findAnnotation<Pattern>()
    val format = prop.findAnnotation<Format>()

    var schema = this

    if (prop.returnType.isMarkedNullable) {
      schema = schema.copy(nullable = true)
    }

    return schema.copy(
      minLength = minLength?.length,
      maxLength = maxLength?.length,
      pattern = pattern?.pattern,
      format = format?.format
    )
  }

  private fun ObjectSchema.scanForConstraints(clazz: KClass<*>, prop: KProperty1<*, *>): ObjectSchema {
    val requiredParams = clazz.primaryConstructor?.parameters?.filterNot { it.isOptional } ?: emptyList()
    var schema = this

    if (requiredParams.isNotEmpty()) {
      schema = schema.copy(required = requiredParams.map { it.name!! })
    }

    if (prop.returnType.isMarkedNullable) {
      schema = schema.copy(nullable = true)
    }

    return schema
  }

  /**
   * If a field has no type parameters, build its [ComponentSchema] without referencing the [TypeMap]
   */
  private fun handleStandardProperty(
    clazz: KClass<*>,
    fieldClazz: KClass<*>,
    prop: KProperty1<*, *>,
    type: KType,
    cache: SchemaMap
  ): ComponentSchema = if (clazz.isSealed) {
    val refs = clazz.sealedSubclasses
      .map { it.createType(type.arguments) }
      .map { cache[it.getSimpleSlug()] ?: error("$it not found in cache") }
    AnyOfSchema(refs)
  } else {
    val slug = fieldClazz.getSimpleSlug(prop)
    cache[slug] ?: error("$slug not found in cache")
  }

  /**
   * If a field has type parameters, leverage the constructed [TypeMap] to construct the [ComponentSchema]
   */
  private fun handleGenericProperty(
    typeMap: TypeMap,
    clazz: KClass<*>,
    type: KType,
    classifier: KClassifier?,
    cache: SchemaMap
  ): ComponentSchema = if (clazz.isSealed) {
    val refs = clazz.sealedSubclasses
      .map { it.createType(type.arguments) }
      .map { it.getSimpleSlug() }
      .map { cache[it] ?: error("$it not available in cache") }
    AnyOfSchema(refs)
  } else {
    val slug = typeMap[classifier]?.type!!.getSimpleSlug()
    cache[slug] ?: error("$slug not found in cache")
  }

  /**
   * Handler for when an [Enum] is encountered
   * @param clazz Class of the object to analyze
   * @param cache Existing schema map to append to
   */
  private fun handleEnumType(clazz: KClass<*>, cache: SchemaMap): SchemaMap {
    val options = clazz.java.enumConstants.map { it.toString() }.toSet()
    return cache.plus(clazz.simpleName!! to EnumSchema(options))
  }

  /**
   * Handler for when a [Map] is encountered
   * @param type Map type information
   * @param clazz Map class information
   * @param cache Existing schema map to append to
   */
  private fun handleMapType(type: KType, clazz: KClass<*>, cache: SchemaMap): SchemaMap {
    logger.debug("Map detected for $type, generating schema and appending to cache")
    val (keyType, valType) = type.arguments.map { it.type }
    logger.debug("Obtained map types -> key: $keyType and value: $valType")
    if (keyType?.classifier != String::class) {
      error("Invalid Map $type: OpenAPI dictionaries must have keys of type String")
    }
    var updatedCache = generateKTypeKontent(valType!!, cache)
    val valClass = valType.classifier as KClass<*>
    val valClassName = valClass.simpleName
    val referenceName = genericNameAdapter(type, clazz)
    val valueReference = when (valClass.isSealed) {
      true -> {
        val subTypes = gatherSubTypes(valType)
        AnyOfSchema(subTypes.map {
          updatedCache = generateKTypeKontent(it, updatedCache)
          updatedCache[it.getSimpleSlug()] ?: error("${it.getSimpleSlug()} not found")
        })
      }
      false -> updatedCache[valClassName] ?: error("$valClassName not found")
    }
    val schema = DictionarySchema(additionalProperties = valueReference)
    updatedCache = generateKontent(valType, updatedCache)
    return updatedCache.plus(referenceName to schema)
  }

  /**
   * Handler for when a [Collection] is encountered
   * @param type Collection type information
   * @param clazz Collection class information
   * @param cache Existing schema map to append to
   */
  private fun handleCollectionType(type: KType, clazz: KClass<*>, cache: SchemaMap): SchemaMap {
    logger.debug("Collection detected for $type, generating schema and appending to cache")
    val collectionType = type.arguments.first().type!!
    val collectionClass = collectionType.classifier as KClass<*>
    logger.debug("Obtained collection class: $collectionClass")
    val referenceName = genericNameAdapter(type, clazz)
    var updatedCache = generateKTypeKontent(collectionType, cache)
    val valueReference = when (collectionClass.isSealed) {
      true -> {
        val subTypes = gatherSubTypes(collectionType)
        AnyOfSchema(subTypes.map {
          updatedCache = generateKTypeKontent(it, cache)
          updatedCache[it.getSimpleSlug()] ?: error("${it.getSimpleSlug()} not found")
        })
      }
      false -> updatedCache[collectionClass.simpleName] ?: error("${collectionClass.simpleName} not found")
    }
    val schema = ArraySchema(items = valueReference)
    updatedCache = generateKontent(collectionType, cache)
    return updatedCache.plus(referenceName to schema)
  }
}
