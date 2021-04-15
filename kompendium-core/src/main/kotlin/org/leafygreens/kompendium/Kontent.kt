package org.leafygreens.kompendium

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import org.leafygreens.kompendium.models.oas.FormatSchema
import org.leafygreens.kompendium.models.oas.ObjectSchema
import org.leafygreens.kompendium.models.oas.OpenApiSpecComponentSchema
import org.leafygreens.kompendium.models.oas.ReferencedSchema
import org.leafygreens.kompendium.models.oas.SimpleSchema
import org.leafygreens.kompendium.util.Helpers.COMPONENT_SLUG
import org.slf4j.LoggerFactory

typealias SchemaMap = Map<String, OpenApiSpecComponentSchema>

internal object Kontent {

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
      clazz.typeParameters.isNotEmpty() -> error("Top level generics are not supported by Kompendium")
      clazz.isSubclassOf(Enum::class) -> TODO("now")
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
            newCache = generateKontent(field as KClass<*>, newCache)
          }
          val propSchema = ReferencedSchema("$COMPONENT_SLUG/${field.simpleName}")
          Pair(prop.name, propSchema)
        }
        logger.info("${clazz.simpleName} contains $fieldMap")
        val schema = ObjectSchema(fieldMap)
        logger.info("${clazz.simpleName} schema: $schema")
        newCache.plus(clazz.simpleName!! to schema)
      }
    }

  private fun generatePropertyKontent(
    prop: KProperty<*>,
    field: KClass<*>,
    cache: SchemaMap
  ): SchemaMap = logged(object {}.javaClass.enclosingMethod.name, mapOf("cache" to cache)) {
    when {
      field.isSubclassOf(Enum::class) -> TODO("enums")
      field.isSubclassOf(Map::class) -> TODO("maps")
      field.isSubclassOf(Collection::class) -> TODO("collection")
      else -> handleComplexType(field, cache)
    }
  }

  // TODO Move to utils
  /**
   * Higher order function that takes a map of names to objects and will log their state ahead of function invocation
   * along with the result of the function invocation
   */
  private fun <T> logged(functionName: String, entities: Map<String, Any>, block: () -> T): T {
    entities.forEach { (name, entity) -> logger.info("Ahead of $functionName invocation, $name: $entity") }
    val result = block.invoke()
    logger.info("Result of $functionName invocation: $result")
    return result
  }

}
