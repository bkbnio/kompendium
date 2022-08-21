package io.bkbn.kompendium.json.schema.handler

import io.bkbn.kompendium.json.schema.SchemaGenerator
import io.bkbn.kompendium.json.schema.SerializableReader
import io.bkbn.kompendium.json.schema.definition.ArrayDefinition
import io.bkbn.kompendium.json.schema.definition.JsonSchema
import io.bkbn.kompendium.json.schema.definition.NullableDefinition
import io.bkbn.kompendium.json.schema.definition.OneOfDefinition
import io.bkbn.kompendium.json.schema.definition.ReferenceDefinition
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.bkbn.kompendium.json.schema.util.Helpers.getReferenceSlug
import io.bkbn.kompendium.json.schema.util.Helpers.getSimpleSlug
import kotlin.reflect.KType

object CollectionHandler {

  fun handle(type: KType, cache: MutableMap<String, JsonSchema>, serializableReader: SerializableReader): JsonSchema {
    val collectionType = type.arguments.first().type
      ?: error("This indicates a bug in Kompendium, please open a GitHub issue!")
    val typeSchema = SchemaGenerator.fromTypeToSchema(collectionType, cache, serializableReader).let {
      if (it is TypeDefinition && it.type == "object") {
        cache[collectionType.getSimpleSlug()] = it
        ReferenceDefinition(collectionType.getReferenceSlug())
      } else {
        it
      }
    }
    val definition = ArrayDefinition(typeSchema)
    return when (type.isMarkedNullable) {
      true -> OneOfDefinition(NullableDefinition(), definition)
      false -> definition
    }
  }

}
