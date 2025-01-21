package io.bkbn.kompendium.json.schema.handler

import io.bkbn.kompendium.enrichment.MapEnrichment
import io.bkbn.kompendium.json.schema.SchemaConfigurator
import io.bkbn.kompendium.json.schema.SchemaGenerator
import io.bkbn.kompendium.json.schema.definition.JsonSchema
import io.bkbn.kompendium.json.schema.definition.MapDefinition
import io.bkbn.kompendium.json.schema.definition.NullableDefinition
import io.bkbn.kompendium.json.schema.definition.OneOfDefinition
import io.bkbn.kompendium.json.schema.definition.ReferenceDefinition
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.bkbn.kompendium.json.schema.util.Helpers.getReferenceSlug
import io.bkbn.kompendium.json.schema.util.Helpers.getSlug
import kotlin.reflect.KClass
import kotlin.reflect.KType

object MapHandler {

  fun handle(
    type: KType,
    cache: MutableMap<String, JsonSchema>,
    schemaConfigurator: SchemaConfigurator,
    enrichment: MapEnrichment<*>? = null
  ): JsonSchema {
    require(enrichment is MapEnrichment<*> || enrichment == null) {
      "Enrichment for map must be either null or a MapEnrichment"
    }
    require(type.arguments.first().type?.classifier as KClass<*> == String::class) {
      "JSON requires that map keys MUST be Strings.  You provided ${type.arguments.first().type}"
    }
    val valueType = type.arguments[1].type ?: error("this indicates a bug in Kompendium, please open a GitHub issue")
    val valueSchema =
      SchemaGenerator.fromTypeToSchema(valueType, cache, schemaConfigurator, enrichment?.valueEnrichment).let {
        if (it is TypeDefinition && it.type == "object") {
          cache[valueType.getSlug(enrichment)] = it
          ReferenceDefinition(valueType.getReferenceSlug(enrichment))
        } else {
          it
        }
      }
    val definition = MapDefinition(valueSchema)
    return when (type.isMarkedNullable) {
      true -> OneOfDefinition(NullableDefinition(), definition)
      false -> definition
    }
  }
}
