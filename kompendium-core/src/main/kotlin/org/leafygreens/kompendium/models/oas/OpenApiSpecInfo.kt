package org.leafygreens.kompendium.models.oas

import java.net.URI

data class OpenApiSpecInfo(
  var title: String? = null,
  var version: String? = null,
  var description: String? = null,
  var termsOfService: URI? = null,
  var contact: OpenApiSpecInfoContact? = null,
  var license: OpenApiSpecInfoLicense? = null
)
