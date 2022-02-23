package io.bkbn.kompendium.core

import io.bkbn.kompendium.annotations.constraint.Format
import io.bkbn.kompendium.core.handler.CollectionHandler
import io.bkbn.kompendium.core.handler.EnumHandler
import io.bkbn.kompendium.core.handler.MapHandler
import io.bkbn.kompendium.core.handler.ObjectHandler
import io.bkbn.kompendium.core.metadata.SchemaMap
import io.bkbn.kompendium.core.util.Helpers.logged
import io.bkbn.kompendium.oas.schema.FormattedSchema
import io.bkbn.kompendium.oas.schema.SimpleSchema
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.math.BigInteger
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.typeOf

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
    cache: SchemaMap = mutableMapOf()
  ) {
    val kontentType = typeOf<T>()
    generateKTypeKontent(kontentType, cache)
  }

  /**
   * Analyzes a [KType] for its top-level and any nested schemas, and adds them to a [SchemaMap], if provided
   * @param type [KType] to analyze
   * @param cache Existing schema map to append to
   * @return an updated schema map containing all type information for [KType] type
   */
  fun generateKontent(
    type: KType,
    cache: SchemaMap = mutableMapOf()
  ) {
    gatherSubTypes(type).forEach {
      generateKTypeKontent(it, cache)
    }
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
    cache: SchemaMap = mutableMapOf(),
  ) = logged(object {}.javaClass.enclosingMethod.name, mapOf("cache" to cache)) {
    logger.debug("Parsing Kontent of $type")
    when (val clazz = type.classifier as KClass<*>) {
      Unit::class -> cache
      Int::class -> cache[clazz.simpleName!!] = FormattedSchema("int32", "integer")
      Long::class -> cache[clazz.simpleName!!] = FormattedSchema("int64", "integer")
      Double::class -> cache[clazz.simpleName!!] = FormattedSchema("double", "number")
      Float::class -> cache[clazz.simpleName!!] = FormattedSchema("float", "number")
      String::class -> cache[clazz.simpleName!!] = SimpleSchema(
        "string",
        format = type.findAnnotation<Format>()?.format
      )
      Boolean::class -> cache[clazz.simpleName!!] = SimpleSchema("boolean")
      UUID::class -> cache[clazz.simpleName!!] = FormattedSchema("uuid", "string")
      BigDecimal::class -> cache[clazz.simpleName!!] = FormattedSchema("double", "number")
      BigInteger::class -> cache[clazz.simpleName!!] = FormattedSchema("int64", "integer")
      ByteArray::class -> cache[clazz.simpleName!!] = FormattedSchema("byte", "string")
      else -> when {
        clazz.isSubclassOf(Collection::class) -> CollectionHandler.handle(type, clazz, cache)
        clazz.isSubclassOf(Enum::class) -> EnumHandler.handle(type, clazz, cache)
        clazz.isSubclassOf(Map::class) -> MapHandler.handle(type, clazz, cache)
        else -> ObjectHandler.handle(type, clazz, cache)
      }
    }
  }
}
