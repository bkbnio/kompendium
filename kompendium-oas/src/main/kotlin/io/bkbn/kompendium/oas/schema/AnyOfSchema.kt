package io.bkbn.kompendium.oas.schema

data class AnyOfSchema(val anyOf: List<ComponentSchema>) : ComponentSchema
