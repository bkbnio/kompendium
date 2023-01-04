package io.bkbn.kompendium.json.schema.handler

import io.bkbn.kompendium.enrichment.TypeEnrichment
import io.bkbn.kompendium.json.schema.definition.EnumDefinition
import io.bkbn.kompendium.json.schema.definition.JsonSchema
import io.bkbn.kompendium.json.schema.definition.ReferenceDefinition
import io.bkbn.kompendium.json.schema.util.Helpers.getReferenceSlug
import io.bkbn.kompendium.json.schema.util.Helpers.getSlug
import kotlin.reflect.KClass
import kotlin.reflect.KType

object EnumHandler {
  fun handle(
    type: KType,
    clazz: KClass<*>,
    cache: MutableMap<String, JsonSchema>,
    enrichment: TypeEnrichment<*>? = null
  ): JsonSchema {
    cache[type.getSlug(enrichment)] = ReferenceDefinition(type.getReferenceSlug(enrichment))

    val options = clazz.java.enumConstants.map { it.toString() }.toSet()
    return EnumDefinition(type = "string", enum = options)
  }
}
