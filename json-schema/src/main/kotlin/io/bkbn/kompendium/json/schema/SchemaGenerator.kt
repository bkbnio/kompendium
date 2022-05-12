package io.bkbn.kompendium.json.schema

import io.bkbn.kompendium.json.schema.handler.ObjectHandler
import kotlin.reflect.KClass
import kotlin.reflect.KType

object SchemaGenerator {

  fun fromType(type: KType): JsonSchema = when (val clazz = type.classifier as KClass<*>) {
    Unit::class -> TODO()
    Int::class -> TypeDefinition.INT
    String::class -> TypeDefinition.STRING
    Boolean::class -> TypeDefinition.BOOLEAN
    else -> ObjectHandler.handle(clazz)
  }

}
