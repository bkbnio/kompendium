package org.leafygreens.kompendium.models

data class OpenApiSpecPathItem(
  val `$ref`: String?, // TODO Maybe drop this?
  val summary: String?,
  val description: String?,
  val get: OpenApiSpecPathItemOperation?,
  val put: OpenApiSpecPathItemOperation?,
  val post: OpenApiSpecPathItemOperation?,
  val delete: OpenApiSpecPathItemOperation?,
  val options: OpenApiSpecPathItemOperation?,
  val head: OpenApiSpecPathItemOperation?,
  val patch: OpenApiSpecPathItemOperation?,
  val trace: OpenApiSpecPathItemOperation?,
  val servers: List<OpenApiSpecServer> = emptyList(),
  val parameters: List<OpenApiSpecReferencable> = emptyList()
)
