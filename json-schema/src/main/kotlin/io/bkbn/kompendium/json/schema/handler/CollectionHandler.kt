package io.bkbn.kompendium.json.schema.handler

import io.bkbn.kompendium.enrichment.CollectionEnrichment
import io.bkbn.kompendium.json.schema.SchemaConfigurator
import io.bkbn.kompendium.json.schema.SchemaGenerator
import io.bkbn.kompendium.json.schema.definition.ArrayDefinition
import io.bkbn.kompendium.json.schema.definition.EnumDefinition
import io.bkbn.kompendium.json.schema.definition.JsonSchema
import io.bkbn.kompendium.json.schema.definition.NullableDefinition
import io.bkbn.kompendium.json.schema.definition.OneOfDefinition
import io.bkbn.kompendium.json.schema.definition.ReferenceDefinition
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.bkbn.kompendium.json.schema.util.Helpers.getReferenceSlug
import io.bkbn.kompendium.json.schema.util.Helpers.getSlug
import kotlin.reflect.KType

object CollectionHandler {
  fun handle(
    type: KType,
    cache: MutableMap<String, JsonSchema>,
    schemaConfigurator: SchemaConfigurator,
    enrichment: CollectionEnrichment<*>? = null
  ): JsonSchema {
    require(enrichment is CollectionEnrichment<*> || enrichment == null) {
      "Enrichment for collection must be either null or a CollectionEnrichment"
    }

    val collectionType = type.arguments.first().type
      ?: error("This indicates a bug in Kompendium, please open a GitHub issue!")
    val typeSchema =
      SchemaGenerator.fromTypeToSchema(collectionType, cache, schemaConfigurator, enrichment?.itemEnrichment).let {
        if ((it is TypeDefinition && it.type == "object") || it is EnumDefinition) {
          cache[collectionType.getSlug(enrichment)] = it
          ReferenceDefinition(collectionType.getReferenceSlug(enrichment))
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
