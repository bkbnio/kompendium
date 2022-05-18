package io.bkbn.kompendium.json.schema.definition

import kotlinx.serialization.Serializable

@Serializable
data class NullableDefinition(val type: String = "null") : JsonSchema
