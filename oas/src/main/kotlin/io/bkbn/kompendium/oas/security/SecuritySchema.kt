package io.bkbn.kompendium.oas.security

import io.bkbn.kompendium.oas.serialization.SecuritySchemaSerializer
import kotlinx.serialization.Serializable

@Serializable(with = SecuritySchemaSerializer::class)
sealed interface SecuritySchema
