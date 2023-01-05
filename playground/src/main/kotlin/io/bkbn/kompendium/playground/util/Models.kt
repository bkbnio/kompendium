package io.bkbn.kompendium.playground.util

import io.ktor.server.locations.KtorExperimentalLocationsAPI
import io.ktor.server.locations.Location
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

@Location("/list/{name}/page/{page}")
data class Listing(val name: String, val page: Int)

@Location("/type/{name}") data class Type(val name: String) {
  // In these classes we have to include the `name` property matching the parent.
  @Location("/edit") data class Edit(val parent: Type)
  @Location("/other/{page}") data class Other(val parent: Type, val page: Int)
}
