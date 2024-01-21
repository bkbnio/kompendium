package io.bkbn.kompendium.json.schema

import io.bkbn.kompendium.json.schema.definition.JsonSchema
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KType

interface SchemaConfigurator {
  fun serializableMemberProperties(clazz: KClass<*>): Collection<KProperty1<out Any, *>>
  fun serializableName(property: KProperty1<out Any, *>): String

  fun sealedObjectEnrichment(
    implementationType: KType,
    implementationSchema: JsonSchema
  ): JsonSchema
}
