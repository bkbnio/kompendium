package io.bkbn.kompendium.oas.info

import io.bkbn.kompendium.oas.serialization.UriSerializer
import kotlinx.serialization.Serializable
import java.net.URI

/**
 * The object provides metadata about the API.
 * The metadata MAY be used by the clients if needed, and MAY be presented in
 * editing or documentation generation tools for convenience.
 *
 * https://spec.openapis.org/oas/v3.1.0#infoObject
 *
 * @param title The title of the API.
 * @param version The version of the OpenAPI document.
 * @param summary A short summary of the API.
 * @param description A description of the API.
 * @param termsOfService A URL to the Terms of Service for the API.
 * @param contact The contact information for the exposed API.
 * @param license The license information for the exposed API.
 */
@Serializable
data class Info(
  val title: String,
  var version: String,
  var summary: String? = null,
  var description: String? = null,
  @Serializable(with = UriSerializer::class)
  var termsOfService: URI? = null,
  var contact: Contact? = null,
  var license: License? = null,
)
