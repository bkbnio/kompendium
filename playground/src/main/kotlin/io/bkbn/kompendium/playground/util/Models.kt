package io.bkbn.kompendium.playground.util

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ExampleRequest(
  val thingA: String,
  val thingB: Int,
  val thingC: InnerRequest,
)

@Serializable
data class InnerRequest(
  val d: Float,
  val e: Boolean,
)

@Serializable
data class ExampleResponse(val isReal: Boolean)

@Serializable
data class CustomTypeResponse(
  val thing: String,
  val timestamp: Instant
)

@Serializable
data class ExceptionResponse(val message: String)
