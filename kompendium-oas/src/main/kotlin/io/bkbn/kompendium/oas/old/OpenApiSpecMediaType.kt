package io.bkbn.kompendium.oas.old

data class OpenApiSpecMediaType<T>(
    val schema: OpenApiSpecReferencable,
    val examples: Map<String, ExampleWrapper<T>>? = null
)

data class ExampleWrapper<T>(val value: T)
