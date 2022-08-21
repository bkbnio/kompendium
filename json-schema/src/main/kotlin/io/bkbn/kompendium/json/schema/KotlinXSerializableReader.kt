package io.bkbn.kompendium.json.schema

import kotlinx.serialization.SerialName
import kotlinx.serialization.Transient
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

class KotlinXSerializableReader: SerializableReader {

  override fun serializableMemberProperties(clazz: KClass<*>): Collection<KProperty1<out Any, *>> =
    clazz.memberProperties
      .filterNot { it.hasAnnotation<Transient>() }

  override fun serializableName(property: KProperty1<out Any, *>): String =
    property.annotations
      .filterIsInstance<SerialName>()
      .firstOrNull()?.value?: property.name
}
