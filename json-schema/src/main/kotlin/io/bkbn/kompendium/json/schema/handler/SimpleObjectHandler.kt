package io.bkbn.kompendium.json.schema.handler

import io.bkbn.kompendium.json.schema.SchemaGenerator
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
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

object SimpleObjectHandler {

  fun handle(type: KType, clazz: KClass<*>, cache: MutableMap<String, JsonSchema>): JsonSchema {
    // cache[type.getSimpleSlug()] = ReferenceDefinition("RECURSION_PLACEHOLDER")
    val typeMap = clazz.typeParameters.zip(type.arguments).toMap()
    val props = clazz.memberProperties.associate { prop ->
      val schema = when (prop.needsToInjectGenerics(typeMap)) {
        true -> handleNestedGenerics(typeMap, prop, cache)
        false -> when (typeMap.containsKey(prop.returnType.classifier)) {
          true -> handleGenericProperty(prop, typeMap, cache)
          false -> handleProperty(prop, cache)
        }
      }

      prop.name to schema
    }

    val required = clazz.memberProperties.filterNot { prop -> prop.returnType.isMarkedNullable }
      .filterNot { prop -> clazz.primaryConstructor!!.parameters.find { it.name == prop.name }!!.isOptional }
      .map { it.name }
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
    cache: MutableMap<String, JsonSchema>
  ): JsonSchema {
    val propClass = prop.returnType.classifier as KClass<*>
    val types = prop.returnType.arguments.map {
      val typeSymbol = it.type.toString()
      typeMap.filterKeys { k -> k.name == typeSymbol }.values.first()
    }
    val constructedType = propClass.createType(types)
    return SchemaGenerator.fromTypeToSchema(constructedType, cache).let {
      if (it is TypeDefinition && it.type == "object") {
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
    cache: MutableMap<String, JsonSchema>
  ): JsonSchema {
    val type = typeMap[prop.returnType.classifier]?.type!!
    return SchemaGenerator.fromTypeToSchema(type, cache).let {
      if (it is TypeDefinition && it.type == "object") {
        cache[type.getSimpleSlug()] = it
        ReferenceDefinition(prop.returnType.getReferenceSlug())
      } else {
        it
      }
    }
  }

  private fun handleProperty(prop: KProperty<*>, cache: MutableMap<String, JsonSchema>): JsonSchema =
    SchemaGenerator.fromTypeToSchema(prop.returnType, cache).let {
      if (it is TypeDefinition && it.type == "object") {
        cache[prop.returnType.getSimpleSlug()] = it
        ReferenceDefinition(prop.returnType.getReferenceSlug())
      } else {
        it
      }
    }
}
