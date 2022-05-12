package io.bkbn.kompendium.oas.info

import io.bkbn.kompendium.oas.serialization.UriSerializer
import kotlinx.serialization.Serializable
import java.net.URI

/**
 * License information for the exposed API.
 *
 * https://spec.openapis.org/oas/v3.1.0#license-object
 *
 * @param name The license name used for the API.
 * @param identifier An SPDX license expression for the API. The identifier field is mutually exclusive of the url field.
 * @param url A URL to the license used for the API.
 *
 */
@Serializable
data class License(
  var name: String,
  var identifier: String? = null,
  @Serializable(with = UriSerializer::class)
  var url: URI? = null
)
