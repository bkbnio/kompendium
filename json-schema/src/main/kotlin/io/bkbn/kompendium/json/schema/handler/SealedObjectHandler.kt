package io.bkbn.kompendium.json.schema.handler

import io.bkbn.kompendium.enrichment.ObjectEnrichment
import io.bkbn.kompendium.json.schema.SchemaConfigurator
import io.bkbn.kompendium.json.schema.SchemaGenerator
import io.bkbn.kompendium.json.schema.definition.AnyOfDefinition
import io.bkbn.kompendium.json.schema.definition.JsonSchema
import io.bkbn.kompendium.json.schema.definition.ReferenceDefinition
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.bkbn.kompendium.json.schema.util.Helpers.getReferenceSlug
import io.bkbn.kompendium.json.schema.util.Helpers.getSlug
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType

object SealedObjectHandler {

  fun handle(
    type: KType,
    clazz: KClass<*>,
    cache: MutableMap<String, JsonSchema>,
    schemaConfigurator: SchemaConfigurator,
    enrichment: ObjectEnrichment<*>? = null,
  ): JsonSchema {
    val subclasses = clazz.sealedSubclasses
      .map { it.createType(type.arguments) }
      .map { t ->
        SchemaGenerator.fromTypeToSchema(t, cache, schemaConfigurator, enrichment)
          .let {
            schemaConfigurator.sealedObjectEnrichment(t, it)
          }.let { js ->
            if (js is TypeDefinition && js.type == "object") {
              val slug = t.getSlug(enrichment)
              cache[slug] = js
              ReferenceDefinition(t.getReferenceSlug(enrichment))
            } else {
              js
            }
          }
      }
      .toSet()
    return AnyOfDefinition(subclasses)
  }
}
