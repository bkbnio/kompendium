package io.bkbn.kompendium.json.schema.definition

import kotlinx.serialization.Serializable

@Serializable
data class ReferenceSchema(val `$ref`: String) : JsonSchema
