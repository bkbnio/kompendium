package io.bkbn.kompendium.json.schema.handler

import io.bkbn.kompendium.json.schema.SchemaGenerator
import io.bkbn.kompendium.json.schema.definition.JsonSchema
import io.bkbn.kompendium.json.schema.definition.NullableDefinition
import io.bkbn.kompendium.json.schema.definition.OneOfDefinition
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.memberProperties

object ObjectHandler {

  fun handle(type: KType, clazz: KClass<*>): JsonSchema {
    val props = clazz.memberProperties.associate { prop ->
      val schema = SchemaGenerator.fromType(prop.returnType)
      prop.name to schema
    }
    val required = clazz.memberProperties.filterNot { prop -> prop.returnType.isMarkedNullable }
      .map { it.name }
      .toSet()
    val definition = TypeDefinition(
      type = "object",
      properties = props,
      required = required
    )

    return when (type.isMarkedNullable) {
      true -> OneOfDefinition(NullableDefinition(), definition)
      false -> definition
    }
  }

}
