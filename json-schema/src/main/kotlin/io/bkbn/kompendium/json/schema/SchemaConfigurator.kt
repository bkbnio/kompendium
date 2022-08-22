package io.bkbn.kompendium.json.schema

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

interface SchemaConfigurator {
  fun serializableMemberProperties(clazz: KClass<*>): Collection<KProperty1<out Any, *>>
  fun serializableName(property: KProperty1<out Any, *>): String

  open class Default: SchemaConfigurator {
    override fun serializableMemberProperties(clazz: KClass<*>): Collection<KProperty1<out Any, *>>
      = clazz.memberProperties
    override fun serializableName(property: KProperty1<out Any, *>): String
      = property.name
  }

}
