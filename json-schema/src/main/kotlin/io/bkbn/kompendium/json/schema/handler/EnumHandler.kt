package io.bkbn.kompendium.json.schema.handler

import io.bkbn.kompendium.json.schema.definition.EnumDefinition
import io.bkbn.kompendium.json.schema.definition.JsonSchema
import io.bkbn.kompendium.json.schema.definition.NullableDefinition
import io.bkbn.kompendium.json.schema.definition.OneOfDefinition
import kotlin.reflect.KClass
import kotlin.reflect.KType

object EnumHandler {

  fun handle(type: KType, clazz: KClass<*>): JsonSchema {
    val options = clazz.java.enumConstants.map { it.toString() }.toSet()
    val definition = EnumDefinition(enum = options)
    return when (type.isMarkedNullable) {
      true -> OneOfDefinition(NullableDefinition(), definition)
      false -> definition
    }
  }

}
