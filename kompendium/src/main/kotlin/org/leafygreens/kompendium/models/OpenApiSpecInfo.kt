package org.leafygreens.kompendium.models

import java.net.URI

data class OpenApiSpecInfo(
  val title: String,
  val version: String,
  val description: String?,
  val termsOfService: URI? = null,
  val contact: OpenApiSpecInfoContact? = null,
  val license: OpenApiSpecInfoLicense? = null
)
