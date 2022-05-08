package io.bkbn.kompendium.oas.server

import io.bkbn.kompendium.oas.serialization.UriSerializer
import kotlinx.serialization.Serializable
import java.net.URI

/**
 * An object representing a Server.
 *
 * https://spec.openapis.org/oas/v3.1.0#server-object
 *
 * @param url A URL to the target host. This URL supports Server Variables and MAY be relative, to indicate that
 * the host location is relative to the location where the OpenAPI document is being served. Variable
 * substitutions will be made when a variable is named in {brackets}.
 * @param description An optional string describing the host designated by the URL.
 * @param variables A map between a variable name and its value.
 * The value is used for substitution in the server’s URL template.
 */
@Serializable
data class Server(
  @Serializable(with = UriSerializer::class)
  val url: URI,
  val description: String? = null,
  var variables: Map<String, ServerVariable>? = null
)
