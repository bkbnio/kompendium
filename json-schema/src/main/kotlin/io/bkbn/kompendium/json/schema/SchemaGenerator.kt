package io.bkbn.kompendium.json.schema

import io.bkbn.kompendium.json.schema.definition.JsonSchema
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.bkbn.kompendium.json.schema.handler.CollectionHandler
import io.bkbn.kompendium.json.schema.handler.EnumHandler
import io.bkbn.kompendium.json.schema.handler.MapHandler
import io.bkbn.kompendium.json.schema.handler.ObjectHandler
import io.bkbn.kompendium.json.schema.util.Helpers.getSimpleSlug
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.typeOf

object SchemaGenerator {
  inline fun <reified T : Any?> fromTypeToSchema(cache: MutableMap<String, JsonSchema> = mutableMapOf()) =
    fromTypeToSchema(typeOf<T>(), cache)

  fun fromTypeToSchema(type: KType, cache: MutableMap<String, JsonSchema>): JsonSchema {
    cache[type.getSimpleSlug()]?.let {
      return it
    }
    return when (val clazz = type.classifier as KClass<*>) {
      Unit::class -> error(
        """
      Unit cannot be converted to JsonSchema.
      If you are looking for a method will return null when called with Unit,
      please call SchemaGenerator.fromTypeOrUnit()
    """.trimIndent()
      )

      Int::class -> TypeDefinition.INT
      String::class -> TypeDefinition.STRING
      Boolean::class -> TypeDefinition.BOOLEAN
      else -> when {
        clazz.isSubclassOf(Enum::class) -> EnumHandler.handle(type, clazz)
        clazz.isSubclassOf(Collection::class) -> CollectionHandler.handle(type, cache)
        clazz.isSubclassOf(Map::class) -> MapHandler.handle(type, cache)
        else -> ObjectHandler.handle(type, clazz, cache)
      }
    }
  }

  fun fromTypeOrUnit(type: KType, cache: MutableMap<String, JsonSchema> = mutableMapOf()): JsonSchema? =
    when (type.classifier as KClass<*>) {
      Unit::class -> null
      else -> fromTypeToSchema(type, cache)
    }
}
