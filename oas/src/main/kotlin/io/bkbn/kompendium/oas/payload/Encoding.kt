package io.bkbn.kompendium.oas.payload

import kotlinx.serialization.Serializable

/**
 * A single encoding definition applied to a single schema property.
 *
 * https://spec.openapis.org/oas/v3.1.0#encoding-object
 *
 * @param contentType The Content-Type for encoding a specific property.
 * @param headers A map allowing additional information to be provided as headers
 * @param style Describes how a specific property value will be serialized depending on its type.
 * @param explode When this is true, property values of type array or object generate separate parameters for each
 * value of the array, or key-value-pair of the map. For other types of properties this property has no effect.
 * @param allowReserved Determines whether the parameter value SHOULD allow reserved characters, as defined by RFC3986
 */
@Serializable
data class Encoding(
  val contentType: String,
  val headers: MutableMap<String, Header>,
  val style: String,
  val explode: Boolean,
  val allowReserved: Boolean = false
)
