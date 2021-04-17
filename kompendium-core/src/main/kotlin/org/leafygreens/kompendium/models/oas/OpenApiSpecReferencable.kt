package org.leafygreens.kompendium.models.oas

sealed class OpenApiSpecReferencable

class OpenApiSpecReferenceObject(val `$ref`: String) : OpenApiSpecReferencable()

data class OpenApiSpecCallback(
  val todo: String // todo fuck me -> https://swagger.io/specification/#callback-object
) : OpenApiSpecReferencable()

data class OpenApiSpecResponse(
    val description: String? = null,
    val headers: Map<String, OpenApiSpecReferencable>? = null,
    val content: Map<String, OpenApiSpecMediaType>? = null,
    val links: Map<String, OpenApiSpecReferencable>? = null
) : OpenApiSpecReferencable()

data class OpenApiSpecHeader(
  val name: String,
  val description: String?,
  val externalDocs: OpenApiSpecExternalDocumentation?
) : OpenApiSpecReferencable()

data class OpenApiSpecParameter(
  val name: String,
  val `in`: String, // TODO Enum? "query", "header", "path" or "cookie"
  val schema: OpenApiSpecSchema,
  val description: String? = null,
  val required: Boolean = true,
  val deprecated: Boolean = false,
  val allowEmptyValue: Boolean? = null,
  val style: String? = null,
  val explode: Boolean? = null
) : OpenApiSpecReferencable()

data class OpenApiSpecRequest(
    val description: String?,
    val content: Map<String, OpenApiSpecMediaType>,
    val required: Boolean = false
) : OpenApiSpecReferencable()
