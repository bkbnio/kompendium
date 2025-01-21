package io.bkbn.kompendium.oas.payload

import io.bkbn.kompendium.json.schema.definition.JsonSchema
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * Each Media Type Object provides schema and examples for the media type identified by its key.
 *
 * https://spec.openapis.org/oas/v3.1.0#media-type-object
 *
 * @param schema The schema defining the content of the request, response, or parameter.
 * @param examples Examples of the media type. Each example object SHOULD match the media type and specified schema if present.
 * @param encoding A map between a property name and its encoding information.
 */
@Serializable
data class MediaType(
  val schema: JsonSchema,
  val examples: Map<String, Example>? = null,
  val encoding: Map<String, Encoding>? = null,
) {
  @Serializable
  data class Example(@Contextual val value: Any, val summary: String? = null, val description: String? = null)
}
