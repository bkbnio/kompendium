package io.bkbn.kompendium.json.schema.handler

import io.bkbn.kompendium.json.schema.SchemaGenerator
import io.bkbn.kompendium.json.schema.definition.AnyOfDefinition
import io.bkbn.kompendium.json.schema.definition.JsonSchema
import io.bkbn.kompendium.json.schema.definition.NullableDefinition
import io.bkbn.kompendium.json.schema.definition.OneOfDefinition
import io.bkbn.kompendium.json.schema.definition.ReferenceDefinition
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.bkbn.kompendium.json.schema.util.Helpers.getReferenceSlug
import io.bkbn.kompendium.json.schema.util.Helpers.getSimpleSlug
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.full.memberProperties

object ObjectHandler {

  fun handle(type: KType, clazz: KClass<*>, cache: MutableMap<String, JsonSchema>): JsonSchema = when (clazz.isSealed) {
    true -> handleSealed(type, clazz, cache)
    false -> handleUnsealed(type, clazz, cache)
  }

  private fun handleSealed(type: KType, clazz: KClass<*>, cache: MutableMap<String, JsonSchema>): JsonSchema {
    val subclasses = clazz.sealedSubclasses
      .map { it.createType(type.arguments) }
      .map { t ->
        SchemaGenerator.fromTypeToSchema(t, cache).let { js ->
          if (js is TypeDefinition && js.type == "object") {
            cache[t.getSimpleSlug()] = js
            ReferenceDefinition(t.getReferenceSlug())
          } else {
            js
          }
        }
      }
      .toSet()
    return AnyOfDefinition(subclasses)
  }

  private fun handleUnsealed(type: KType, clazz: KClass<*>, cache: MutableMap<String, JsonSchema>): JsonSchema {
    val typeMap = clazz.typeParameters.zip(type.arguments).toMap()
    val props = clazz.memberProperties.associate { prop ->
      val schema = when (typeMap.containsKey(prop.returnType.classifier)) {
        true -> handleGenericProperty(prop, typeMap, cache)
        false -> handleProperty(prop, cache)
      }

      prop.name to schema
    }
    val required = clazz.memberProperties.filterNot { prop -> prop.returnType.isMarkedNullable }
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
