package io.bkbn.kompendium.core.attribute

import io.bkbn.kompendium.json.schema.definition.JsonSchema
import io.bkbn.kompendium.oas.OpenApiSpec
import io.ktor.util.AttributeKey
import kotlin.reflect.KClass

object KompendiumAttributes {
  val cache = AttributeKey<MutableMap<KClass<*>, JsonSchema>>("KompendiumCache")
  val openApiSpec = AttributeKey<OpenApiSpec>("OpenApiSpec")
}
