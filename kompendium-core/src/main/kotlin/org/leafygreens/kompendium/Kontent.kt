package org.leafygreens.kompendium

import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import kotlin.reflect.typeOf
import org.leafygreens.kompendium.models.meta.SchemaMap
import org.leafygreens.kompendium.models.oas.ArraySchema
import org.leafygreens.kompendium.models.oas.DictionarySchema
import org.leafygreens.kompendium.models.oas.EnumSchema
import org.leafygreens.kompendium.models.oas.FormatSchema
import org.leafygreens.kompendium.models.oas.ObjectSchema
import org.leafygreens.kompendium.models.oas.ReferencedSchema
import org.leafygreens.kompendium.models.oas.SimpleSchema
import org.leafygreens.kompendium.util.Helpers.COMPONENT_SLUG
import org.leafygreens.kompendium.util.Helpers.genericNameAdapter
import org.leafygreens.kompendium.util.Helpers.getReferenceSlug
import org.leafygreens.kompendium.util.Helpers.logged
import org.slf4j.LoggerFactory

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
   * Analyze a type [T], but filters out the top-level type
   * @param T type to analyze
   * @param cache Existing schema map to append to
   * @return an updated schema map containing all type information for [T]
   */
  @OptIn(ExperimentalStdlibApi::class)
  inline fun <reified T> generateParameterKontent(
    cache: SchemaMap = emptyMap()
  ): SchemaMap {
    val kontentType = typeOf<T>()
    return generateKTypeKontent(kontentType, cache)
      .filterNot { (slug, _) -> slug == (kontentType.classifier as KClass<*>).simpleName }
  }

  /**
   * Recursively fills schema map depending on [KType] classifier
   * @param type [KType] to parse
   * @param cache Existing schema map to append to
   */
  fun generateKTypeKontent(
    type: KType,
    cache: SchemaMap = emptyMap()
  ): SchemaMap = logged(object {}.javaClass.enclosingMethod.name, mapOf("cache" to cache)) {
    logger.debug("Parsing Kontent of $type")
    when (val clazz = type.classifier as KClass<*>) {
      Unit::class -> cache
      Int::class -> cache.plus(clazz.simpleName!! to FormatSchema("int32", "integer"))
      Long::class -> cache.plus(clazz.simpleName!! to FormatSchema("int64", "integer"))
      Double::class -> cache.plus(clazz.simpleName!! to FormatSchema("double", "number"))
      Float::class -> cache.plus(clazz.simpleName!! to FormatSchema("float", "number"))
      String::class -> cache.plus(clazz.simpleName!! to SimpleSchema("string"))
      Boolean::class -> cache.plus(clazz.simpleName!! to SimpleSchema("boolean"))
      UUID::class -> cache.plus(clazz.simpleName!! to FormatSchema("uuid", "string"))
      else -> when {
        clazz.isSubclassOf(Collection::class) -> handleCollectionType(type, clazz, cache)
        clazz.isSubclassOf(Enum::class) -> handleEnumType(clazz, cache)
        clazz.isSubclassOf(Map::class) -> handleMapType(type, clazz, cache)
        else -> handleComplexType(clazz, cache)
      }
    }
  }

  /**
   * In the event of an object type, this method will parse out individual fields to recursively aggregate object map.
   * @param clazz Class of the object to analyze
   * @param cache Existing schema map to append to
   */
  private fun handleComplexType(clazz: KClass<*>, cache: SchemaMap): SchemaMap =
    when (cache.containsKey(clazz.simpleName)) {
      true -> {
        logger.debug("Cache already contains ${clazz.simpleName}, returning cache untouched")
        cache
      }
      false -> {
        logger.debug("${clazz.simpleName} was not found in cache, generating now")
        var newCache = cache
        val fieldMap = clazz.memberProperties.associate { prop ->
          logger.debug("Analyzing $prop in class $clazz")
          val field = prop.javaField?.type?.kotlin ?: error("Unable to parse field type from $prop")
          logger.debug("Detected field $field")
          if (!newCache.containsKey(field.simpleName)) {
            logger.debug("Cache was missing ${field.simpleName}, adding now")
            newCache = generateKTypeKontent(prop.returnType, newCache)
          }
          val propSchema = ReferencedSchema(field.getReferenceSlug(prop))
          Pair(prop.name, propSchema)
        }
        logger.debug("${clazz.simpleName} contains $fieldMap")
        val schema = ObjectSchema(fieldMap)
        logger.debug("${clazz.simpleName} schema: $schema")
        newCache.plus(clazz.simpleName!! to schema)
      }
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
    val valClassName = (valType?.classifier as KClass<*>).simpleName
    val referenceName = genericNameAdapter(type, clazz)
    val valueReference = ReferencedSchema("$COMPONENT_SLUG/$valClassName")
    val schema = DictionarySchema(additionalProperties = valueReference)
    val updatedCache = generateKTypeKontent(valType, cache)
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
    val valueReference = ReferencedSchema("${COMPONENT_SLUG}/${collectionClass.simpleName}")
    val schema = ArraySchema(items = valueReference)
    val updatedCache = generateKTypeKontent(collectionType, cache)
    return updatedCache.plus(referenceName to schema)
  }
}
