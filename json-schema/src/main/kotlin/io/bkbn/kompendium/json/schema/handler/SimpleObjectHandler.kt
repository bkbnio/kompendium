package io.bkbn.kompendium.json.schema.handler

import io.bkbn.kompendium.enrichment.Enrichment
import io.bkbn.kompendium.enrichment.ObjectEnrichment
import io.bkbn.kompendium.json.schema.SchemaConfigurator
import io.bkbn.kompendium.json.schema.SchemaGenerator
import io.bkbn.kompendium.json.schema.definition.EnumDefinition
import io.bkbn.kompendium.json.schema.definition.JsonSchema
import io.bkbn.kompendium.json.schema.definition.NullableDefinition
import io.bkbn.kompendium.json.schema.definition.OneOfDefinition
import io.bkbn.kompendium.json.schema.definition.ReferenceDefinition
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.bkbn.kompendium.json.schema.exception.UnknownSchemaException
import io.bkbn.kompendium.json.schema.handler.EnrichmentHandler.applyToSchema
import io.bkbn.kompendium.json.schema.util.Helpers.getReferenceSlug
import io.bkbn.kompendium.json.schema.util.Helpers.getSlug
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaField

object SimpleObjectHandler {

  fun handle(
    type: KType,
    clazz: KClass<*>,
    cache: MutableMap<String, JsonSchema>,
    schemaConfigurator: SchemaConfigurator,
    enrichment: ObjectEnrichment<*>?,
  ): JsonSchema {
    require(enrichment is ObjectEnrichment<*> || enrichment == null) {
      "Enrichment for object must either be of type ObjectEnrichment or null"
    }

    val slug = type.getSlug(enrichment)
    val referenceSlug = type.getReferenceSlug(enrichment)
    cache[slug] = ReferenceDefinition(referenceSlug)

    val typeMap = clazz.typeParameters.zip(type.arguments).toMap()
    val props = schemaConfigurator.serializableMemberProperties(clazz)
      .filterNot { it.javaField == null }
      .associate { prop ->
        val propEnrichment = enrichment?.propertyEnrichment?.get(prop)

        val schema = when (prop.needsToInjectGenerics(typeMap)) {
          true -> handleNestedGenerics(typeMap, prop, cache, schemaConfigurator, propEnrichment)
          false -> when (typeMap.containsKey(prop.returnType.classifier)) {
            true -> handleGenericProperty(prop, typeMap, cache, schemaConfigurator, propEnrichment)
            false -> handleProperty(prop, cache, schemaConfigurator, propEnrichment)
          }
        }

        val enrichedSchema = propEnrichment?.applyToSchema(schema) ?: schema

        val nullCheckSchema = when (prop.returnType.isMarkedNullable && !enrichedSchema.isNullable()) {
          true -> OneOfDefinition(NullableDefinition(), enrichedSchema)
          false -> enrichedSchema
        }

        schemaConfigurator.serializableName(prop) to nullCheckSchema
      }

    val required = schemaConfigurator.serializableMemberProperties(clazz)
      .asSequence()
      .filterNot { it.javaField == null }
      .filterNot { prop -> prop.returnType.isMarkedNullable }
      .filterNot { prop ->
        clazz.primaryConstructor
          ?.parameters
          ?.find { it.name == prop.name }
          ?.isOptional
          ?: throw UnknownSchemaException(
            """
            |An unknown type was encountered: $clazz.  This typically indicates that a complex scalar such as dates,
            |timestamps, or custom number representations such as BigInteger were not added as custom types when
            |configuring the NotarizedApplication plugin.  If you are still seeing this error despite adding all
            |required custom types, this indicates a bug in Kompendium, please open an issue on GitHub.
            """.trimMargin()
          )
      }
      .map { schemaConfigurator.serializableName(it) }
      .toSet()

    val definition = TypeDefinition(
      type = "object",
      properties = props,
      required = required
    )

    cache[slug] = definition
    return definition
  }

  private fun KProperty<*>.needsToInjectGenerics(
    typeMap: Map<KTypeParameter, KTypeProjection>
  ): Boolean {
    val typeSymbols = returnType.arguments.map { it.type.toString() }
    return typeMap.any { (k, _) -> typeSymbols.contains(k.name) }
  }

  private fun handleNestedGenerics(
    typeMap: Map<KTypeParameter, KTypeProjection>,
    prop: KProperty<*>,
    cache: MutableMap<String, JsonSchema>,
    schemaConfigurator: SchemaConfigurator,
    propEnrichment: Enrichment?
  ): JsonSchema {
    val propClass = prop.returnType.classifier as KClass<*>
    val types = prop.returnType.arguments.map {
      val typeSymbol = it.type.toString()
      typeMap.filterKeys { k -> k.name == typeSymbol }.values.first()
    }
    val constructedType = propClass.createType(types)
    return SchemaGenerator.fromTypeToSchema(constructedType, cache, schemaConfigurator, propEnrichment)
      .let {
        if (it.isOrContainsObjectOrEnumDef()) {
          cache[constructedType.getSlug(propEnrichment)] = it
          ReferenceDefinition(prop.returnType.getReferenceSlug(propEnrichment))
        } else {
          it
        }
      }
  }

  private fun handleGenericProperty(
    prop: KProperty<*>,
    typeMap: Map<KTypeParameter, KTypeProjection>,
    cache: MutableMap<String, JsonSchema>,
    schemaConfigurator: SchemaConfigurator,
    propEnrichment: Enrichment?
  ): JsonSchema {
    val type = typeMap[prop.returnType.classifier]?.type
      ?: error("This indicates a bug in Kompendium, please open a GitHub issue")
    return SchemaGenerator.fromTypeToSchema(type, cache, schemaConfigurator, propEnrichment).let {
      if (it.isOrContainsObjectOrEnumDef()) {
        cache[type.getSlug(propEnrichment)] = it
        ReferenceDefinition(type.getReferenceSlug(propEnrichment))
      } else {
        it
      }
    }
  }

  private fun handleProperty(
    prop: KProperty<*>,
    cache: MutableMap<String, JsonSchema>,
    schemaConfigurator: SchemaConfigurator,
    propEnrichment: Enrichment?
  ): JsonSchema =
    SchemaGenerator.fromTypeToSchema(prop.returnType, cache, schemaConfigurator, propEnrichment).let {
      if (it.isOrContainsObjectOrEnumDef()) {
        cache[prop.returnType.getSlug(propEnrichment)] = it
        ReferenceDefinition(prop.returnType.getReferenceSlug(propEnrichment))
      } else {
        it
      }
    }

  private fun JsonSchema.isOrContainsObjectOrEnumDef(): Boolean {
    val isTypeDef = this is TypeDefinition && type == "object"
    val isTypeDefOneOf = this is OneOfDefinition && this.oneOf.any { js -> js is TypeDefinition && js.type == "object" }
    val isEnumDef = this is EnumDefinition
    val isEnumDefOneOf = this is OneOfDefinition && this.oneOf.any { js -> js is EnumDefinition }
    return isTypeDef || isTypeDefOneOf || isEnumDef || isEnumDefOneOf
  }

  private fun JsonSchema.isNullable(): Boolean = this is OneOfDefinition && this.oneOf.any { it is NullableDefinition }
}
