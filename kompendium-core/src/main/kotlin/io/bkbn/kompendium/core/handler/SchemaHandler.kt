package io.bkbn.kompendium.core.handler

import io.bkbn.kompendium.core.metadata.SchemaMap
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType

interface SchemaHandler {
  fun handle(type: KType, clazz: KClass<*>, cache: SchemaMap)

  fun gatherSubTypes(type: KType): List<KType> {
    val classifier = type.classifier as KClass<*>
    return if (classifier.isSealed) {
      classifier.sealedSubclasses.map {
        it.createType(type.arguments)
      }
    } else {
      listOf(type)
    }
  }
}
