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
import org.leafygreens.kompendium.util.Helpers
import org.leafygreens.kompendium.util.Helpers.COMPONENT_SLUG
import org.leafygreens.kompendium.util.Helpers.genericNameAdapter
import org.leafygreens.kompendium.util.Helpers.getReferenceSlug
import org.slf4j.LoggerFactory

object Kontent {

  private val logger = LoggerFactory.getLogger(javaClass)

  @OptIn(ExperimentalStdlibApi::class)
  inline fun <reified T> generateKontent(
    cache: SchemaMap = emptyMap()
  ): SchemaMap {
    val kontentType = typeOf<T>()
    return generateKTypeKontent(kontentType, cache)
  }

  fun generateKTypeKontent(
    type: KType,
    cache: SchemaMap = emptyMap()
  ): SchemaMap = Helpers.logged(object {}.javaClass.enclosingMethod.name, mapOf("cache" to cache)) {
    logger.info("Parsing Kontent of $type")
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

  private fun handleComplexType(clazz: KClass<*>, cache: SchemaMap): SchemaMap =
    when (cache.containsKey(clazz.simpleName)) {
      true -> {
        logger.info("Cache already contains ${clazz.simpleName}, returning cache untouched")
        cache
      }
      false -> {
        logger.info("${clazz.simpleName} was not found in cache, generating now")
        var newCache = cache
        val fieldMap = clazz.memberProperties.associate { prop ->
          logger.info("Analyzing $prop in class $clazz")
          val field = prop.javaField?.type?.kotlin ?: error("Unable to parse field type from $prop")
          logger.info("Detected field $field")
          if (!newCache.containsKey(field.simpleName)) {
            logger.info("Cache was missing ${field.simpleName}, adding now")
            newCache = generateKTypeKontent(prop.returnType, newCache)
          }
          val propSchema = ReferencedSchema(field.getReferenceSlug(prop))
          Pair(prop.name, propSchema)
        }
        logger.info("${clazz.simpleName} contains $fieldMap")
        val schema = ObjectSchema(fieldMap)
        logger.info("${clazz.simpleName} schema: $schema")
        newCache.plus(clazz.simpleName!! to schema)
      }
    }

  private fun handleEnumType(clazz: KClass<*>, cache: SchemaMap): SchemaMap {
    val options = clazz.java.enumConstants.map { it.toString() }.toSet()
    return cache.plus(clazz.simpleName!! to EnumSchema(options))
  }

  private fun handleMapType(type: KType, clazz: KClass<*>, cache: SchemaMap): SchemaMap {
    logger.info("Map detected for $type, generating schema and appending to cache")
    val (keyType, valType) = type.arguments.map { it.type }
    logger.info("Obtained map types -> key: $keyType and value: $valType")
    if (keyType?.classifier != String::class) {
      error("Invalid Map $type: OpenAPI dictionaries must have keys of type String")
    }
    val valClassName = (valType?.classifier as KClass<*>).simpleName
    val referenceName = genericNameAdapter(type, clazz)
    val valueReference = ReferencedSchema("$COMPONENT_SLUG/$valClassName")
    val schema = DictionarySchema(additionalProperties = valueReference)
    val updatedCache = generateKTypeKontent(valType!!, cache)
    return updatedCache.plus(referenceName to schema)
  }

  private fun handleCollectionType(type: KType, clazz: KClass<*>, cache: SchemaMap): SchemaMap {
    logger.info("Collection detected for $type, generating schema and appending to cache")
    val collectionType = type.arguments.first().type!!
    val collectionClass = collectionType.classifier as KClass<*>
    logger.info("Obtained collection class: $collectionClass")
    val referenceName = genericNameAdapter(type, clazz)
    val valueReference = ReferencedSchema("${COMPONENT_SLUG}/${collectionClass.simpleName}")
    val schema = ArraySchema(items = valueReference)
    val updatedCache = generateKTypeKontent(collectionType, cache)
    return updatedCache.plus(referenceName to schema)
  }
}
