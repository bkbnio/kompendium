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

class KotlinXSchemaConfigurator : SchemaConfigurator {

  override fun serializableMemberProperties(clazz: KClass<*>): Collection<KProperty1<out Any, *>> =
    clazz.memberProperties
      .filterNot { it.hasAnnotation<Transient>() }

  override fun serializableName(property: KProperty1<out Any, *>): String =
    property.annotations
      .filterIsInstance<SerialName>()
      .firstOrNull()?.value ?: property.name

  override fun sealedTypeEnrichment(
    implementationType: KType,
    implementationSchema: JsonSchema,
  ): JsonSchema {
    return if (implementationSchema is TypeDefinition && implementationSchema.type == "object") {
      implementationSchema.copy(
        required = implementationSchema.required?.plus("type"),
        properties = implementationSchema.properties?.plus(
          mapOf(
            "type" to EnumDefinition("string", enum = setOf(determineTypeQualifier(implementationType)))
          )
        )
      )
    } else {
      implementationSchema
    }
  }

  private fun determineTypeQualifier(type: KType): String {
    val nameOverrideAnnotation = type.findAnnotation<SerialName>()
    // TODO Cleaner way to get fqcn?
    return nameOverrideAnnotation?.value ?: type.classifier.toString().replace("class ", "")
  }
}
