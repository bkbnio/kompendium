package io.bkbn.kompendium.json.schema.handler

import io.bkbn.kompendium.enrichment.BooleanEnrichment
import io.bkbn.kompendium.enrichment.CollectionEnrichment
import io.bkbn.kompendium.enrichment.Enrichment
import io.bkbn.kompendium.enrichment.MapEnrichment
import io.bkbn.kompendium.enrichment.NumberEnrichment
import io.bkbn.kompendium.enrichment.ObjectEnrichment
import io.bkbn.kompendium.enrichment.StringEnrichment
import io.bkbn.kompendium.json.schema.definition.ArrayDefinition
import io.bkbn.kompendium.json.schema.definition.JsonSchema
import io.bkbn.kompendium.json.schema.definition.MapDefinition
import io.bkbn.kompendium.json.schema.definition.ReferenceDefinition
import io.bkbn.kompendium.json.schema.definition.TypeDefinition

object EnrichmentHandler {

  fun Enrichment.applyToSchema(schema: JsonSchema): JsonSchema = when (this) {
    is BooleanEnrichment -> applyToSchema(schema)
    is NumberEnrichment -> applyToSchema(schema)
    is StringEnrichment -> applyToSchema(schema)
    is CollectionEnrichment<*> -> applyToSchema(schema)
    is MapEnrichment<*> -> applyToSchema(schema)
    is ObjectEnrichment<*> -> applyToSchema(schema)
    else -> error("Incorrect enrichment type for enrichment id: ${this.id}")
  }

  private fun ObjectEnrichment<*>.applyToSchema(schema: JsonSchema): JsonSchema = when (schema) {
    is TypeDefinition -> schema.copy(deprecated = deprecated, description = description)
    is ReferenceDefinition -> schema.copy(deprecated = deprecated, description = description)
    else -> error("Incorrect enrichment type for enrichment id: ${this.id}")
  }

  private fun MapEnrichment<*>.applyToSchema(schema: JsonSchema): JsonSchema = when (schema) {
    is MapDefinition -> schema.copyMapEnrichment(this)
    else -> error("Incorrect enrichment type for enrichment id: ${this.id}")
  }

  private fun CollectionEnrichment<*>.applyToSchema(schema: JsonSchema): JsonSchema = when (schema) {
    is ArrayDefinition -> schema.copyArrayEnrichment(this)
    else -> error("Incorrect enrichment type for enrichment id: ${this.id}")
  }

  private fun BooleanEnrichment.applyToSchema(schema: JsonSchema): JsonSchema = when (schema) {
    is TypeDefinition -> schema.copyBooleanEnrichment(this)
    else -> error("Incorrect enrichment type for enrichment id: ${this.id}")
  }

  private fun NumberEnrichment.applyToSchema(schema: JsonSchema): JsonSchema = when (schema) {
    is TypeDefinition -> schema.copyNumberEnrichment(this)
    else -> error("Incorrect enrichment type for enrichment id: ${this.id}")
  }

  private fun StringEnrichment.applyToSchema(schema: JsonSchema): JsonSchema = when (schema) {
    is TypeDefinition -> schema.copyStringEnrichment(this)
    else -> error("Incorrect enrichment type for enrichment id: ${this.id}")
  }

  private fun TypeDefinition.copyBooleanEnrichment(
    enrichment: BooleanEnrichment
  ): TypeDefinition = copy(
    deprecated = enrichment.deprecated,
    description = enrichment.description,
  )

  private fun TypeDefinition.copyNumberEnrichment(
    enrichment: NumberEnrichment
  ): TypeDefinition = copy(
    deprecated = enrichment.deprecated,
    description = enrichment.description,
    multipleOf = enrichment.multipleOf,
    maximum = enrichment.maximum,
    exclusiveMaximum = enrichment.exclusiveMaximum,
    minimum = enrichment.minimum,
    exclusiveMinimum = enrichment.exclusiveMinimum,
  )

  private fun TypeDefinition.copyStringEnrichment(
    enrichment: StringEnrichment
  ): TypeDefinition = copy(
    deprecated = enrichment.deprecated,
    description = enrichment.description,
    maxLength = enrichment.maxLength,
    minLength = enrichment.minLength,
    pattern = enrichment.pattern,
    contentEncoding = enrichment.contentEncoding,
    contentMediaType = enrichment.contentMediaType,
  )

  private fun ArrayDefinition.copyArrayEnrichment(
    enrichment: CollectionEnrichment<*>
  ): ArrayDefinition = copy(
    deprecated = enrichment.deprecated,
    description = enrichment.description,
    minItems = enrichment.minItems,
    maxItems = enrichment.maxItems,
    uniqueItems = enrichment.uniqueItems,
  )

  private fun MapDefinition.copyMapEnrichment(
    enrichment: MapEnrichment<*>
  ): MapDefinition = copy(
    deprecated = enrichment.deprecated,
    description = enrichment.description,
    minProperties = enrichment.minProperties,
    maxProperties = enrichment.maxProperties,
  )
}
