package org.leafygreens.kompendium.models

data class OpenApiSpecPathItem(
  // val `$ref`: String?, // TODO need example of this... or just make whole thing referencable?
  val summary: String? = null,
  val description: String? = null,
  val get: OpenApiSpecPathItemOperation? = null,
  val put: OpenApiSpecPathItemOperation? = null,
  val post: OpenApiSpecPathItemOperation? = null,
  val delete: OpenApiSpecPathItemOperation? = null,
  val options: OpenApiSpecPathItemOperation? = null,
  val head: OpenApiSpecPathItemOperation? = null,
  val patch: OpenApiSpecPathItemOperation? = null,
  val trace: OpenApiSpecPathItemOperation? = null,
  val servers: List<OpenApiSpecServer>? = null,
  val parameters: List<OpenApiSpecReferencable>? = null
)
