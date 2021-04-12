package org.leafygreens.kompendium.models

data class OpenApiSpecPathItem(
  var summary: String? = null,
  var description: String? = null,
  var get: OpenApiSpecPathItemOperation? = null,
  var put: OpenApiSpecPathItemOperation? = null,
  var post: OpenApiSpecPathItemOperation? = null,
  var delete: OpenApiSpecPathItemOperation? = null,
  var options: OpenApiSpecPathItemOperation? = null,
  var head: OpenApiSpecPathItemOperation? = null,
  var patch: OpenApiSpecPathItemOperation? = null,
  var trace: OpenApiSpecPathItemOperation? = null,
  var servers: List<OpenApiSpecServer>? = null,
  var parameters: List<OpenApiSpecReferencable>? = null
)
