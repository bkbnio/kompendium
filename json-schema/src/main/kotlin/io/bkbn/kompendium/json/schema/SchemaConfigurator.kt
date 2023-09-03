package io.bkbn.kompendium.json.schema

import io.bkbn.kompendium.json.schema.definition.JsonSchema
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.memberProperties

interface SchemaConfigurator {
  fun serializableMemberProperties(clazz: KClass<*>): Collection<KProperty1<out Any, *>>
  fun serializableName(property: KProperty1<out Any, *>): String

  fun sealedTypeEnrichment(
    implementationType: KType,
    implementationSchema: JsonSchema
  ): JsonSchema = implementationSchema

  open class Default : SchemaConfigurator {
    override fun serializableMemberProperties(clazz: KClass<*>): Collection<KProperty1<out Any, *>> =
      clazz.memberProperties

    override fun serializableName(property: KProperty1<out Any, *>): String = property.name
  }
}
