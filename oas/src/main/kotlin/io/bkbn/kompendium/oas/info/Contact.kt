package io.bkbn.kompendium.oas.info

import io.bkbn.kompendium.oas.serialization.UriSerializer
import kotlinx.serialization.Serializable
import java.net.URI

/**
 * Contact information for the exposed API.
 *
 * https://spec.openapis.org/oas/v3.1.0#contactObject
 *
 * @param name The identifying name of the contact person/organization.
 * @param url The URL pointing to the contact information.
 * @param email The email address of the contact person/organization.
 */
@Serializable
data class Contact(
  var name: String,
  @Serializable(with = UriSerializer::class)
  var url: URI? = null,
  var email: String? = null
)
