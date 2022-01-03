package oas.payload

import oas.schema.ComponentSchema

data class MediaType<T>(
  val schema: ComponentSchema,
  val examples: Map<String, Example<T>>? = null
) {
  data class Example<T>(val value: T)
}
