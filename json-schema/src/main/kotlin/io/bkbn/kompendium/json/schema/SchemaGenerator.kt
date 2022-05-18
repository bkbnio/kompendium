package io.bkbn.kompendium.json.schema

import io.bkbn.kompendium.json.schema.definition.JsonSchema
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.bkbn.kompendium.json.schema.handler.CollectionHandler
import io.bkbn.kompendium.json.schema.handler.EnumHandler
import io.bkbn.kompendium.json.schema.handler.MapHandler
import io.bkbn.kompendium.json.schema.handler.ObjectHandler
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.typeOf

object SchemaGenerator {

  inline fun <reified T> fromType() = fromType(typeOf<T>())

  fun fromType(type: KType): JsonSchema? = when (val clazz = type.classifier as KClass<*>) {
    // TODO Make this an error instead, then can get rid of null checks.. makes sense to delegate this to client
    Unit::class -> null
    Int::class -> TypeDefinition.INT
    String::class -> TypeDefinition.STRING
    Boolean::class -> TypeDefinition.BOOLEAN
    else -> when {
      clazz.isSubclassOf(Enum::class) -> EnumHandler.handle(clazz)
      clazz.isSubclassOf(Collection::class) -> CollectionHandler.handle(type)
      clazz.isSubclassOf(Map::class) -> MapHandler.handle(type)
      else -> ObjectHandler.handle(clazz)
    }
  }

}
