package io.bkbn.kompendium.oas.payload

import io.bkbn.kompendium.oas.schema.ComponentSchema
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class Parameter(
  val name: String,
  val `in`: String, // TODO Enum? "query", "header", "path" or "cookie"
  val schema: ComponentSchema,
  val description: String? = null,
  val required: Boolean = true,
  val deprecated: Boolean = false,
  val allowEmptyValue: Boolean? = null,
  val style: String? = null,
  val explode: Boolean? = null,
  val examples: Map<String, Example>? = null
) {
  @Serializable
  data class Example(val value: @Contextual Any)
}
