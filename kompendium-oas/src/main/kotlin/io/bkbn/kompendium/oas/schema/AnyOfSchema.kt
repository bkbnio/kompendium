package io.bkbn.kompendium.oas.schema

data class AnyOfSchema(val anyOf: List<ComponentSchema>, override val description: String? = null) : ComponentSchema
