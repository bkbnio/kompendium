package io.bkbn.kompendium.json.schema

import io.bkbn.kompendium.enrichment.CollectionEnrichment
import io.bkbn.kompendium.enrichment.Enrichment
import io.bkbn.kompendium.enrichment.MapEnrichment
import io.bkbn.kompendium.enrichment.ObjectEnrichment
import io.bkbn.kompendium.enrichment.TypeEnrichment
import io.bkbn.kompendium.json.schema.definition.JsonSchema
import io.bkbn.kompendium.json.schema.definition.NullableDefinition
import io.bkbn.kompendium.json.schema.definition.OneOfDefinition
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.bkbn.kompendium.json.schema.handler.CollectionHandler
import io.bkbn.kompendium.json.schema.handler.EnumHandler
import io.bkbn.kompendium.json.schema.handler.MapHandler
import io.bkbn.kompendium.json.schema.handler.SealedObjectHandler
import io.bkbn.kompendium.json.schema.handler.SimpleObjectHandler
import io.bkbn.kompendium.json.schema.util.Helpers.getSlug
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf

object SchemaGenerator {

  @Suppress("CyclomaticComplexMethod")
  fun fromTypeToSchema(
    type: KType,
    cache: MutableMap<String, JsonSchema>,
    schemaConfigurator: SchemaConfigurator,
    enrichment: Enrichment? = null
  ): JsonSchema {
    val slug = type.getSlug(enrichment)

    cache[slug]?.let {
      return it
    }

    return when (val clazz = type.classifier as KClass<*>) {
      Unit::class -> error(
        """
          Unit cannot be converted to JsonSchema.
          If you are looking for a method will return null when called with Unit,
          please call SchemaGenerator.fromTypeOrUnit()
        """.trimIndent()
      )

      Int::class -> checkForNull(type, TypeDefinition.INT)
      Long::class -> checkForNull(type, TypeDefinition.LONG)
      Double::class -> checkForNull(type, TypeDefinition.DOUBLE)
      Float::class -> checkForNull(type, TypeDefinition.FLOAT)
      String::class -> checkForNull(type, TypeDefinition.STRING)
      Boolean::class -> checkForNull(type, TypeDefinition.BOOLEAN)
      UUID::class -> checkForNull(type, TypeDefinition.UUID)
      else -> complexTypeToSchema(clazz, type, cache, schemaConfigurator, enrichment as? TypeEnrichment<*>?)
    }
  }

  fun fromTypeOrUnit(
    type: KType,
    cache: MutableMap<String, JsonSchema> = mutableMapOf(),
    schemaConfigurator: SchemaConfigurator,
    enrichment: TypeEnrichment<*>? = null
  ): JsonSchema? =
    when (type.classifier as KClass<*>) {
      Unit::class -> null
      else -> fromTypeToSchema(type, cache, schemaConfigurator, enrichment)
    }

  private fun checkForNull(type: KType, schema: JsonSchema): JsonSchema = when (type.isMarkedNullable) {
    true -> OneOfDefinition(NullableDefinition(), schema)
    false -> schema
  }

  private fun complexTypeToSchema(
    clazz: KClass<*>,
    type: KType,
    cache: MutableMap<String, JsonSchema>,
    schemaConfigurator: SchemaConfigurator,
    enrichment: Enrichment? = null
  ): JsonSchema = when {
    clazz.isSubclassOf(Enum::class) -> EnumHandler.handle(type, clazz, cache)
    clazz.isSubclassOf(Collection::class) -> handleCollection(type, cache, schemaConfigurator, enrichment)
    clazz.isSubclassOf(Map::class) -> handleMap(type, cache, schemaConfigurator, enrichment)
    else -> handleObject(type, clazz, cache, schemaConfigurator, enrichment)
  }

  private fun handleCollection(
    type: KType,
    cache: MutableMap<String, JsonSchema>,
    schemaConfigurator: SchemaConfigurator,
    enrichment: Enrichment?
  ) = when (enrichment) {
    is CollectionEnrichment<*> -> CollectionHandler.handle(type, cache, schemaConfigurator, enrichment)
    null -> CollectionHandler.handle(type, cache, schemaConfigurator, null)
    else -> error("Incorrect enrichment type for enrichment id: ${enrichment.id}")
  }

  private fun handleMap(
    type: KType,
    cache: MutableMap<String, JsonSchema>,
    schemaConfigurator: SchemaConfigurator,
    enrichment: Enrichment?
  ) = when (enrichment) {
    is MapEnrichment<*> -> MapHandler.handle(type, cache, schemaConfigurator, enrichment)
    null -> MapHandler.handle(type, cache, schemaConfigurator, null)
    else -> error("Incorrect enrichment type for enrichment id: ${enrichment.id}")
  }

  private fun handleObject(
    type: KType,
    clazz: KClass<*>,
    cache: MutableMap<String, JsonSchema>,
    schemaConfigurator: SchemaConfigurator,
    enrichment: Enrichment?
  ) = when (clazz.isSealed) {
    true -> when (enrichment) {
      is ObjectEnrichment<*> -> SealedObjectHandler.handle(type, clazz, cache, schemaConfigurator, enrichment)
      null -> SealedObjectHandler.handle(type, clazz, cache, schemaConfigurator, null)
      else -> error("Incorrect enrichment type for enrichment id: ${enrichment.id}")
    }

    false -> when (enrichment) {
      is ObjectEnrichment<*> -> SimpleObjectHandler.handle(type, clazz, cache, schemaConfigurator, enrichment)
      null -> SimpleObjectHandler.handle(type, clazz, cache, schemaConfigurator, null)
      else -> error("Incorrect enrichment type for enrichment id: ${enrichment.id}")
    }
  }
}
