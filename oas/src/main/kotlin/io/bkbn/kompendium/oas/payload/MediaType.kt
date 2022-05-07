package io.bkbn.kompendium.oas.payload

import io.bkbn.kompendium.oas.schema.ComponentSchema
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class MediaType(
  val schema: ComponentSchema,
  val examples: Map<String, Example>? = null
) {
  @Serializable
  data class Example(val value: @Contextual Any)
}
