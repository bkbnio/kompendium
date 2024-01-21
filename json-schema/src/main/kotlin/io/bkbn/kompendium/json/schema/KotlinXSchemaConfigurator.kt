package io.bkbn.kompendium.json.schema

import io.bkbn.kompendium.json.schema.definition.EnumDefinition
import io.bkbn.kompendium.json.schema.definition.JsonSchema
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import kotlinx.serialization.SerialName
import kotlinx.serialization.Transient
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

class KotlinXSchemaConfigurator : SchemaConfigurator {

  override fun serializableMemberProperties(clazz: KClass<*>): Collection<KProperty1<out Any, *>> {
    return clazz.memberProperties
      .filterNot { it.hasAnnotation<Transient>() }
      .filter { clazz.primaryConstructor?.parameters?.map { it.name }?.contains(it.name) ?: true }
  }

  override fun serializableName(property: KProperty1<out Any, *>): String =
    property.annotations
      .filterIsInstance<SerialName>()
      .firstOrNull()?.value ?: property.name

  override fun sealedObjectEnrichment(
    implementationType: KType,
    implementationSchema: JsonSchema,
  ): JsonSchema {
    return if (implementationSchema is TypeDefinition && implementationSchema.type == "object") {
      implementationSchema.copy(
        required = implementationSchema.required?.plus("type"),
        properties = implementationSchema.properties?.plus(
          mapOf(
            "type" to EnumDefinition(enum = setOf(determineTypeQualifier(implementationType)))
          )
        )
      )
    } else {
      implementationSchema
    }
  }

  private fun determineTypeQualifier(type: KType): String {
    val nameOverrideAnnotation = (type.classifier as KClass<*>).findAnnotation<SerialName>()
    return nameOverrideAnnotation?.value ?: (type.classifier as KClass<*>).qualifiedName!!
  }
}
