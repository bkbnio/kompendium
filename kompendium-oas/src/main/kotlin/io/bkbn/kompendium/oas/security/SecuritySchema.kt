package io.bkbn.kompendium.oas.security

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("schema_type") // todo figure out a way to filter this
sealed interface SecuritySchema
