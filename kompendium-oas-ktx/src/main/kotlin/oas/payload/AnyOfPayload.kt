package oas.payload

import oas.schema.ComponentSchema

data class AnyOfPayload(val anyOf: List<ComponentSchema>) : Payload
