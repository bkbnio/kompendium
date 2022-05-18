package io.bkbn.kompendium.oas.component

import io.bkbn.kompendium.json.schema.definition.JsonSchema
import io.bkbn.kompendium.oas.security.SecuritySchema
import kotlinx.serialization.Serializable

/**
 * Holds a set of reusable objects for different aspects of the OAS. All objects defined within the components object
 * will have no effect on the API unless they are explicitly referenced from properties outside the components object.
 *
 * https://spec.openapis.org/oas/v3.1.0#components-object
 */
@Serializable
data class Components(
    val schemas: MutableMap<String, JsonSchema> = mutableMapOf(),
    val securitySchemes: MutableMap<String, SecuritySchema> = mutableMapOf()
)
