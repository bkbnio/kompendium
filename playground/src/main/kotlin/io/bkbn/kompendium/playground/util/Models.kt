package io.bkbn.kompendium.playground.util

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ExampleResponse(val isReal: Boolean)

@Serializable
data class CustomTypeResponse(
  val thing: String,
  val timestamp: Instant
)

@Serializable
data class ExceptionResponse(val message: String)
