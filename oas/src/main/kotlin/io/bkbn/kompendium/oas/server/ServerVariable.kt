package io.bkbn.kompendium.oas.server

import kotlinx.serialization.Serializable

/**
 * An object representing a Server Variable for server URL template substitution.
 *
 * https://spec.openapis.org/oas/v3.1.0#serverVariableObject
 *
 * @param enum An enumeration of string values to be used if the substitution options are from a limited set. The array MUST NOT be empty.
 * @param default The default value to use for substitution, which SHALL be sent if an alternate value is not supplied.
 * @param description An optional description for the server variable.
 */
@Serializable
data class ServerVariable(
  val `enum`: Set<String>, // todo enforce not empty
  val default: String,
  val description: String?
)
