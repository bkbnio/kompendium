package io.bkbn.kompendium.models.oas

data class OpenApiSpecMediaType<T>(
  val schema: OpenApiSpecReferenceObject,
  val examples: Map<String, ExampleWrapper<T>>? = null
)

data class ExampleWrapper<T>(val value: T)
