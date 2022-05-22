package io.bkbn.kompendium.json.schema.util

import io.bkbn.kompendium.json.schema.definition.JsonSchema
import kotlin.reflect.KType

data class ReferenceCache(
  val referenceRootPath: String = "#/",
  val cache: MutableMap<KType, JsonSchema> = mutableMapOf()
)
