package oas.payload

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import oas.schema.ComponentSchema

@Serializable
data class MediaType(
  val schema: ComponentSchema,
  val examples: Map<String, Example>? = null
) {
  @Serializable
  data class Example(val value: @Contextual Any)
}
