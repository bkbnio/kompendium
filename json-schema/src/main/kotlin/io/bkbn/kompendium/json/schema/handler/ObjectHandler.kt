package io.bkbn.kompendium.json.schema.handler

import io.bkbn.kompendium.json.schema.JsonSchema
import io.bkbn.kompendium.json.schema.SchemaGenerator
import io.bkbn.kompendium.json.schema.TypeDefinition
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

object ObjectHandler {

  fun handle(clazz: KClass<*>): JsonSchema {
    val props = clazz.memberProperties.associate { prop ->
      prop.name to SchemaGenerator.fromType(prop.returnType)
    }
    val required = clazz.memberProperties.filterNot { prop -> prop.returnType.isMarkedNullable }
      .map { it.name }
      .toSet()
    return TypeDefinition(
      type = "object",
      properties = props,
      required = required
    )
  }

}
