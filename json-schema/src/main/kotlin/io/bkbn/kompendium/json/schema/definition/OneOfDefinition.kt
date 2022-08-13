package io.bkbn.kompendium.json.schema.definition

import kotlinx.serialization.Serializable

@Serializable
data class OneOfDefinition(val oneOf: Set<JsonSchema>) : JsonSchema {
  constructor(vararg types: JsonSchema) : this(types.toSet())
}
