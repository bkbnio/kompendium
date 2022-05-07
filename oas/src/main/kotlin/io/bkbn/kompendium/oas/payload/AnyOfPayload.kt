package io.bkbn.kompendium.oas.payload

import io.bkbn.kompendium.oas.schema.ComponentSchema

data class AnyOfPayload(val anyOf: List<ComponentSchema>) : Payload
