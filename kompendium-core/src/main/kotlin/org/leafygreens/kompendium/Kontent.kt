package org.leafygreens.kompendium

import java.lang.reflect.ParameterizedType
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import org.leafygreens.kompendium.models.meta.SchemaMap
import org.leafygreens.kompendium.models.oas.ArraySchema
import org.leafygreens.kompendium.models.oas.DictionarySchema
import org.leafygreens.kompendium.models.oas.EnumSchema
import org.leafygreens.kompendium.models.oas.FormatSchema
import org.leafygreens.kompendium.models.oas.ObjectSchema
import org.leafygreens.kompendium.models.oas.OpenApiSpecComponentSchema
import org.leafygreens.kompendium.models.oas.ReferencedSchema
import org.leafygreens.kompendium.models.oas.SimpleSchema
import org.leafygreens.kompendium.util.Helpers.COMPONENT_SLUG
import org.leafygreens.kompendium.util.Helpers.genericNameAdapter
import org.leafygreens.kompendium.util.Helpers.logged
import org.leafygreens.kompendium.util.Helpers.toPair
import org.slf4j.LoggerFactory

object Kontent {

  private val logger = LoggerFactory.getLogger(javaClass)

  fun generateKontent(
    clazz: KClass<*>,
    cache: SchemaMap = emptyMap()
  ): SchemaMap = logged(object {}.javaClass.enclosingMethod.name, mapOf("cache" to cache)) {
    when {
      clazz == Unit::class -> cache
      clazz == Int::class -> cache.plus(clazz.simpleName!! to FormatSchema("int32", "integer"))
      clazz == Long::class -> cache.plus(clazz.simpleName!! to FormatSchema("int64", "integer"))
      clazz == Double::class -> cache.plus(clazz.simpleName!! to FormatSchema("double", "number"))
      clazz == Float::class -> cache.plus(clazz.simpleName!! to FormatSchema("float", "number"))
      clazz == String::class -> cache.plus(clazz.simpleName!! to SimpleSchema("string"))
      clazz == Boolean::class -> cache.plus(clazz.simpleName!! to SimpleSchema("boolean"))
      clazz == UUID::class -> cache.plus(clazz.simpleName!! to FormatSchema("uuid", "string"))
      clazz.isSubclassOf(Enum::class) -> error("Top level enums are currently not supported by Kompendium")
      clazz.typeParameters.isNotEmpty() -> error("Top level generics are not supported by Kompendium")
      else -> handleComplexType(clazz, cache)
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
            newCache = generateFieldKontent(prop, field, newCache)
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

  private fun KClass<*>.getReferenceSlug(prop: KProperty<*>): String = when {
    this.typeParameters.isNotEmpty() -> "$COMPONENT_SLUG/${genericNameAdapter(this, prop)}"
    else -> "$COMPONENT_SLUG/${simpleName}"
  }

  private fun generateFieldKontent(
    prop: KProperty<*>,
    field: KClass<*>,
    cache: SchemaMap
  ): SchemaMap = logged(object {}.javaClass.enclosingMethod.name, mapOf("cache" to cache)) {
    when {
      field.isSubclassOf(Enum::class) -> enumFieldHandler(prop, field, cache)
      field.isSubclassOf(Map::class) -> mapFieldHandler(prop, field, cache)
      field.isSubclassOf(Collection::class) -> collectionFieldHandler(prop, field, cache)
      else -> generateKontent(field, cache)
    }
  }

  private fun enumFieldHandler(prop: KProperty<*>, field: KClass<*>, cache: SchemaMap): SchemaMap {
    logger.info("Enum detected for $prop, gathering values")
    val options = prop.javaField?.type?.enumConstants?.map { it.toString() }?.toSet()
      ?: error("unable to parse enum $prop")
    return cache.plus(field.simpleName!! to EnumSchema(options))
  }

  private fun mapFieldHandler(prop: KProperty<*>, field: KClass<*>, cache: SchemaMap): SchemaMap {
    logger.info("Map detected for $prop, generating schema and appending to cache")
    val (keyClass, valClass) = (prop.javaField?.genericType as ParameterizedType)
      .actualTypeArguments.slice(IntRange(0, 1))
      .map { it as Class<*> }
      .map { it.kotlin }
      .toPair()
    if (keyClass != String::class) error("Invalid Map $prop: OpenAPI dictionaries must have keys of type String")
    val referenceName = genericNameAdapter(field, prop)
    val valueReference = ReferencedSchema("$COMPONENT_SLUG/${valClass.simpleName}")
    val schema = DictionarySchema(additionalProperties = valueReference)
    val updatedCache = generateKontent(valClass, cache)
    return updatedCache.plus(referenceName to schema)
  }

  private fun collectionFieldHandler(prop: KProperty<*>, field: KClass<*>, cache: SchemaMap): SchemaMap {
    logger.info("Collection detected for $prop, generating schema and appending to cache")
    val collectionClass = ((prop.javaField?.genericType as ParameterizedType)
      .actualTypeArguments.first() as Class<*>).kotlin
    logger.info("Obtained collection class: $collectionClass")
    val referenceName = genericNameAdapter(field, prop)
    val valueReference = ReferencedSchema("$COMPONENT_SLUG/${collectionClass.simpleName}")
    val schema = ArraySchema(items = valueReference)
    val updatedCache = generateKontent(collectionClass, cache)
    return updatedCache.plus(referenceName to schema)
  }

}
