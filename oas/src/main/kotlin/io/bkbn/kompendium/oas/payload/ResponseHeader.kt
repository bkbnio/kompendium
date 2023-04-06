package io.bkbn.kompendium.oas.payload

import io.bkbn.kompendium.json.schema.definition.JsonSchema
import kotlinx.serialization.Serializable

/**
 * Describes a header object
 * https://spec.openapis.org/oas/v3.1.0#header-object
 *
 * @param description A brief description of the parameter.
 * This is valid only for query parameters and allows sending a parameter with an empty value.
 */
@Serializable
data class ResponseHeader(
  val schema: JsonSchema,
  val description: String? = null
  // todo support styling https://spec.openapis.org/oas/v3.1.0#style-values
)
