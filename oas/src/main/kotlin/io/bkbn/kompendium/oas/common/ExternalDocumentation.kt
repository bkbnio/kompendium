package io.bkbn.kompendium.oas.common

import io.bkbn.kompendium.oas.serialization.UriSerializer
import kotlinx.serialization.Serializable
import java.net.URI

/**
 * Allows referencing an external resource for extended documentation.
 *
 * https://spec.openapis.org/oas/v3.1.0#external-documentation-object
 *
 * @param url The URL for the target documentation.
 * @param description A description of the target documentation.
 */
@Serializable
data class ExternalDocumentation(
  @Serializable(with = UriSerializer::class)
  val url: URI,
  val description: String?
)
