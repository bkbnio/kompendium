package io.bkbn.kompendium.core.handler

import io.bkbn.kompendium.core.legacy.metadata.SchemaMap
import io.bkbn.kompendium.core.util.Helpers.COMPONENT_SLUG
import io.bkbn.kompendium.oas.schema.ComponentSchema
import io.bkbn.kompendium.oas.schema.EnumSchema
import io.bkbn.kompendium.oas.schema.ObjectSchema
import io.bkbn.kompendium.oas.schema.ReferencedSchema
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

  fun postProcessSchema(schema: ComponentSchema, slug: String?): ComponentSchema = when (schema) {
    is ObjectSchema -> {
      require(slug != null) { "Slug cannot be null for an object schema! $schema" }
      ReferencedSchema(COMPONENT_SLUG.plus("/").plus(slug))
    }
    is EnumSchema -> {
      require(slug != null) { "Slug cannot be null for an enum schema! $schema" }
      ReferencedSchema(COMPONENT_SLUG.plus("/").plus(slug))
    }
    else -> schema
  }
}
