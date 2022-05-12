package io.bkbn.kompendium.json.schema.handler

import io.bkbn.kompendium.json.schema.JsonSchema
import io.bkbn.kompendium.json.schema.SchemaGenerator
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

object ObjectHandler {

  fun handle(clazz: KClass<*>): JsonSchema {
    val props = clazz.memberProperties.associate { prop ->
      prop.name to SchemaGenerator.fromType(prop.returnType)
    }
    return JsonSchema(type = "object", properties = props)
  }

}
