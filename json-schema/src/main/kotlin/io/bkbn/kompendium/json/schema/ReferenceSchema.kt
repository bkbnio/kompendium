package io.bkbn.kompendium.json.schema

import kotlinx.serialization.Serializable

@Serializable
data class ReferenceSchema(val `$ref`: String) : JsonSchema
