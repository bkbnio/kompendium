package org.leafygreens.kompendium.models

sealed class OpenApiSpecReferencable

class OpenApiSpecReferenceObject(val `$ref`: String) : OpenApiSpecReferencable()

data class OpenApiSpecCallback(
  val todo: String // todo fuck me -> https://swagger.io/specification/#callback-object
) : OpenApiSpecReferencable()

data class OpenApiSpecResponse(
  val description: String,
  val headers: Map<String, OpenApiSpecReferencable>,
  val content: Map<String, OpenApiSpecMediaType>,
  val links: Map<String, OpenApiSpecReferencable>
) : OpenApiSpecReferencable()

data class OpenApiSpecHeader(
  val name: String,
  val description: String?,
  val externalDocs: OpenApiSpecExternalDocumentation?
) : OpenApiSpecReferencable()

data class OpenApiSpecParameter(
  val name: String,
  val `in`: String, // TODO Enum? "query", "header", "path" or "cookie"
  val description: String?,
  val required: Boolean = true,
  val deprecated: Boolean = false,
  val allowEmptyValue: Boolean = false
) : OpenApiSpecReferencable()
