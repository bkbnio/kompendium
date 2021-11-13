package io.bkbn.kompendium.oas.old

// TODO Rename
sealed interface OpenApiSpecReferencable

data class OpenApiAnyOf(val anyOf: List<OpenApiSpecComponentSchema>) : OpenApiSpecReferencable

data class OpenApiSpecResponse<T>(
    val description: String? = null,
    val headers: Map<String, OpenApiSpecReferencable>? = null,
    val content: Map<String, OpenApiSpecMediaType<T>>? = null,
    val links: Map<String, OpenApiSpecReferencable>? = null
) : OpenApiSpecReferencable

data class OpenApiSpecParameter(
    val name: String,
    val `in`: String, // TODO Enum? "query", "header", "path" or "cookie"
    val schema: OpenApiSpecComponentSchema,
    val description: String? = null,
    val required: Boolean = true,
    val deprecated: Boolean = false,
    val allowEmptyValue: Boolean? = null,
    val style: String? = null,
    val explode: Boolean? = null
) : OpenApiSpecReferencable

data class OpenApiSpecRequest<T>(
    val description: String?,
    val content: Map<String, OpenApiSpecMediaType<T>>,
    val required: Boolean = false
) : OpenApiSpecReferencable
