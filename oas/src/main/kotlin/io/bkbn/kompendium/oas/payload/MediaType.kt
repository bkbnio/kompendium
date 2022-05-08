package io.bkbn.kompendium.oas.payload

import io.bkbn.kompendium.oas.schema.ComponentSchema
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * Each Media Type Object provides schema and examples for the media type identified by its key.
 *
 * https://spec.openapis.org/oas/v3.1.0#media-type-object
 *
 * @param schema The schema defining the content of the request, response, or parameter.
 * @param examples Examples of the media type. Each example object SHOULD match the media type and specified schema if present.
 */
@Serializable
data class MediaType(
  val schema: ComponentSchema,
  val examples: Map<String, Example>? = null,
  val encoding: Map<String, Encoding>
) {
  @Serializable
  data class Example(val value: @Contextual Any)
}
