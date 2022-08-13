package io.bkbn.kompendium.json.schema

import io.bkbn.kompendium.json.schema.definition.JsonSchema
import io.bkbn.kompendium.json.schema.definition.NullableDefinition
import io.bkbn.kompendium.json.schema.definition.OneOfDefinition
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.bkbn.kompendium.json.schema.handler.CollectionHandler
import io.bkbn.kompendium.json.schema.handler.EnumHandler
import io.bkbn.kompendium.json.schema.handler.MapHandler
import io.bkbn.kompendium.json.schema.handler.SimpleObjectHandler
import io.bkbn.kompendium.json.schema.handler.SealedObjectHandler
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
      Int::class -> checkForNull(type, TypeDefinition.INT)
      Long::class -> checkForNull(type, TypeDefinition.LONG)
      Double::class -> checkForNull(type, TypeDefinition.DOUBLE)
      Float::class -> checkForNull(type, TypeDefinition.FLOAT)
      String::class -> checkForNull(type, TypeDefinition.STRING)
      Boolean::class -> checkForNull(type, TypeDefinition.BOOLEAN)
      else -> when {
        clazz.isSubclassOf(Enum::class) -> EnumHandler.handle(type, clazz)
        clazz.isSubclassOf(Collection::class) -> CollectionHandler.handle(type, cache)
        clazz.isSubclassOf(Map::class) -> MapHandler.handle(type, cache)
        else -> {
          if (clazz.isSealed) {
            SealedObjectHandler.handle(type, clazz, cache)
          } else {
            SimpleObjectHandler.handle(type, clazz, cache)
          }
        }
      }
    }
  }

  fun fromTypeOrUnit(type: KType, cache: MutableMap<String, JsonSchema> = mutableMapOf()): JsonSchema? =
    when (type.classifier as KClass<*>) {
      Unit::class -> null
      else -> fromTypeToSchema(type, cache)
    }

  private fun checkForNull(type: KType, schema: JsonSchema): JsonSchema = when (type.isMarkedNullable) {
    true -> OneOfDefinition(NullableDefinition(), schema)
    false -> schema
  }
}
