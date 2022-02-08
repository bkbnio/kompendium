package io.bkbn.kompendium.core

import io.bkbn.kompendium.core.metadata.SchemaMap
import io.bkbn.kompendium.core.schema.CollectionHandler
import io.bkbn.kompendium.core.schema.EnumHandler
import io.bkbn.kompendium.core.schema.MapHandler
import io.bkbn.kompendium.core.schema.ObjectHandler
import io.bkbn.kompendium.core.util.Helpers.logged
import io.bkbn.kompendium.oas.schema.FormattedSchema
import io.bkbn.kompendium.oas.schema.SimpleSchema
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubclassOf
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
        clazz.isSubclassOf(Collection::class) -> CollectionHandler.handle(type, clazz, cache)
        clazz.isSubclassOf(Enum::class) -> EnumHandler.handle(type, clazz, cache)
        clazz.isSubclassOf(Map::class) -> MapHandler.handle(type, clazz, cache)
        else -> ObjectHandler.handle(type, clazz, cache)
      }
    }
  }
}
