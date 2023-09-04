package io.bkbn.kompendium.oas.payload

import io.bkbn.kompendium.json.schema.definition.JsonSchema
import kotlinx.serialization.Serializable

/**
 * Describes a single operation parameter
 * https://spec.openapis.org/oas/v3.1.0#parameter-object
 *
 * @param name The name of the parameter. Parameter names are case-sensitive.
 * @param in The location of the parameter.
 * @param description A brief description of the parameter.
 * @param required Determines whether this parameter is mandatory. If the parameter location is "path",
 * this property is REQUIRED and its value MUST be true. Otherwise, the property MAY be included and its default value is false.
 * @param deprecated Specifies that a parameter is deprecated and SHOULD be transitioned out of usage.
 * @param allowEmptyValue Sets the ability to pass empty-valued parameters.
 * This is valid only for query parameters and allows sending a parameter with an empty value.
 */
@Serializable
data class Parameter(
  val name: String,
  val `in`: Location,
  val schema: JsonSchema,
  val description: String? = null,
  val required: Boolean = true,
  val deprecated: Boolean = false,
  val allowEmptyValue: Boolean? = null,
  val examples: Map<String, MediaType.Example>? = null,
  // todo support styling https://spec.openapis.org/oas/v3.1.0#style-values
) {
  @Suppress("EnumNaming")
  @Serializable
  enum class Location {
    query,
    header,
    path,
    cookie
  }
}
