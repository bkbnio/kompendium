package io.bkbn.kompendium.json.schema.handler

import io.bkbn.kompendium.json.schema.SchemaGenerator
import io.bkbn.kompendium.json.schema.SchemaConfigurator
import io.bkbn.kompendium.json.schema.definition.JsonSchema
import io.bkbn.kompendium.json.schema.definition.NullableDefinition
import io.bkbn.kompendium.json.schema.definition.OneOfDefinition
import io.bkbn.kompendium.json.schema.definition.ReferenceDefinition
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.bkbn.kompendium.json.schema.util.Helpers.getReferenceSlug
import io.bkbn.kompendium.json.schema.util.Helpers.getSimpleSlug
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
    schemaConfigurator: SchemaConfigurator
  ): JsonSchema {

    cache[type.getSimpleSlug()] = ReferenceDefinition(type.getReferenceSlug())

    val typeMap = clazz.typeParameters.zip(type.arguments).toMap()
    val props = schemaConfigurator.serializableMemberProperties(clazz)
      .filterNot { it.javaField == null }
      .associate { prop ->
      val schema = when (prop.needsToInjectGenerics(typeMap)) {
        true -> handleNestedGenerics(typeMap, prop, cache, schemaConfigurator)
        false -> when (typeMap.containsKey(prop.returnType.classifier)) {
          true -> handleGenericProperty(prop, typeMap, cache, schemaConfigurator)
          false -> handleProperty(prop, cache, schemaConfigurator)
        }
      }

      val nullCheckSchema = when (prop.returnType.isMarkedNullable && !schema.isNullable()) {
        true -> OneOfDefinition(NullableDefinition(), schema)
        false -> schema
      }

      schemaConfigurator.serializableName(prop) to nullCheckSchema
    }

    val required = schemaConfigurator.serializableMemberProperties(clazz)
      .asSequence()
      .filterNot { it.javaField == null }
      .filterNot { prop -> prop.returnType.isMarkedNullable }
      .filterNot { prop -> clazz.primaryConstructor!!.parameters.find { it.name == prop.name }!!.isOptional }
      .map { schemaConfigurator.serializableName(it) }
      .toSet()

    val definition = TypeDefinition(
      type = "object",
      properties = props,
      required = required
    )

    return when (type.isMarkedNullable) {
      true -> OneOfDefinition(NullableDefinition(), definition)
      false -> definition
    }
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
    schemaConfigurator: SchemaConfigurator
  ): JsonSchema {
    val propClass = prop.returnType.classifier as KClass<*>
    val types = prop.returnType.arguments.map {
      val typeSymbol = it.type.toString()
      typeMap.filterKeys { k -> k.name == typeSymbol }.values.first()
    }
    val constructedType = propClass.createType(types)
    return SchemaGenerator.fromTypeToSchema(constructedType, cache, schemaConfigurator).let {
      if (it.isOrContainsObjectDef()) {
        cache[constructedType.getSimpleSlug()] = it
        ReferenceDefinition(prop.returnType.getReferenceSlug())
      } else {
        it
      }
    }
  }

  private fun handleGenericProperty(
    prop: KProperty<*>,
    typeMap: Map<KTypeParameter, KTypeProjection>,
    cache: MutableMap<String, JsonSchema>,
    schemaConfigurator: SchemaConfigurator
  ): JsonSchema {
    val type = typeMap[prop.returnType.classifier]?.type
      ?: error("This indicates a bug in Kompendium, please open a GitHub issue")
    return SchemaGenerator.fromTypeToSchema(type, cache, schemaConfigurator).let {
      if (it.isOrContainsObjectDef()) {
        cache[type.getSimpleSlug()] = it
        ReferenceDefinition(type.getReferenceSlug())
      } else {
        it
      }
    }
  }

  private fun handleProperty(
    prop: KProperty<*>,
    cache: MutableMap<String, JsonSchema>,
    schemaConfigurator: SchemaConfigurator
  ): JsonSchema =
    SchemaGenerator.fromTypeToSchema(prop.returnType, cache, schemaConfigurator).let {
      if (it.isOrContainsObjectDef()) {
        cache[prop.returnType.getSimpleSlug()] = it
        ReferenceDefinition(prop.returnType.getReferenceSlug())
      } else {
        it
      }
    }

  private fun JsonSchema.isOrContainsObjectDef(): Boolean {
    val isTypeDef = this is TypeDefinition && type == "object"
    val isTypeDefOneOf = this is OneOfDefinition && this.oneOf.any { js -> js is TypeDefinition && js.type == "object" }
    return isTypeDef || isTypeDefOneOf
  }

  private fun JsonSchema.isNullable(): Boolean = this is OneOfDefinition && this.oneOf.any { it is NullableDefinition }
}
