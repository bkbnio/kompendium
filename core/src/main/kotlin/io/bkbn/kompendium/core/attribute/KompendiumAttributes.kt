package io.bkbn.kompendium.core.attribute

import io.bkbn.kompendium.oas.OpenApiSpec
import io.bkbn.kompendium.oas.schema.ComponentSchema
import io.ktor.util.AttributeKey
import kotlin.reflect.KClass

object KompendiumAttributes {
  val cache = AttributeKey<MutableMap<KClass<*>, ComponentSchema>>("KompendiumCache")
  val openApiSpec = AttributeKey<OpenApiSpec>("OpenApiSpec")
}
