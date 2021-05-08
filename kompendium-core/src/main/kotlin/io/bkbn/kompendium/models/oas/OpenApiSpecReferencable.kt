package io.bkbn.kompendium.models.oas

sealed class OpenApiSpecReferencable

class OpenApiSpecReferenceObject(val `$ref`: String) : OpenApiSpecReferencable()

data class OpenApiSpecResponse<T>(
    val description: String? = null,
    val headers: Map<String, OpenApiSpecReferencable>? = null,
    val content: Map<String, OpenApiSpecMediaType<T>>? = null,
    val links: Map<String, OpenApiSpecReferencable>? = null
) : OpenApiSpecReferencable()

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
) : OpenApiSpecReferencable()

data class OpenApiSpecRequest<T>(
    val description: String?,
    val content: Map<String, OpenApiSpecMediaType<T>>,
    val required: Boolean = false
) : OpenApiSpecReferencable()
