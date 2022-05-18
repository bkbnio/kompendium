package io.bkbn.kompendium.json.schema.handler

import io.bkbn.kompendium.json.schema.SchemaGenerator
import io.bkbn.kompendium.json.schema.definition.ArrayDefinition
import io.bkbn.kompendium.json.schema.definition.JsonSchema
import io.bkbn.kompendium.json.schema.definition.NullableDefinition
import io.bkbn.kompendium.json.schema.definition.OneOfDefinition
import kotlin.reflect.KType

object CollectionHandler {

  fun handle(type: KType): JsonSchema {
    val collectionType = type.arguments.first().type!!
    val typeSchema = SchemaGenerator.fromType(collectionType)
    val definition = ArrayDefinition(typeSchema)
    return when (type.isMarkedNullable) {
      true -> OneOfDefinition(NullableDefinition(), definition)
      false -> definition
    }
  }

}
