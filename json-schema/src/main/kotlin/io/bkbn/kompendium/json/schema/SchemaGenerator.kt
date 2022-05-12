package io.bkbn.kompendium.json.schema

import io.bkbn.kompendium.json.schema.handler.ObjectHandler
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

object SchemaGenerator {

  inline fun <reified T> fromType() = fromType(typeOf<T>())

  fun fromType(type: KType): JsonSchema = when (val clazz = type.classifier as KClass<*>) {
    Unit::class -> TODO()
    Int::class -> JsonSchema.INT
    String::class -> JsonSchema.STRING
    Boolean::class -> JsonSchema.BOOLEAN
    else -> ObjectHandler.handle(clazz)
  }

}
