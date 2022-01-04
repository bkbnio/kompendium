package io.bkbn.kompendium.oas.schema

import kotlinx.serialization.Serializable

@Serializable
data class AnyOfSchema(val anyOf: List<ComponentSchema>, override val description: String? = null) : ComponentSchema
