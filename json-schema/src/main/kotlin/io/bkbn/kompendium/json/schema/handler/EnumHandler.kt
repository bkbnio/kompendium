package io.bkbn.kompendium.json.schema.handler

import io.bkbn.kompendium.json.schema.definition.EnumDefinition
import io.bkbn.kompendium.json.schema.definition.JsonSchema
import kotlin.reflect.KClass

object EnumHandler {

  fun handle(clazz: KClass<*>): JsonSchema {
    val options = clazz.java.enumConstants.map { it.toString() }.toSet()
    return EnumDefinition(enum = options)
  }

}
