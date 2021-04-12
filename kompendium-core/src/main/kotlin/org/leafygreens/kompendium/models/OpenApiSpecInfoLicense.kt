package org.leafygreens.kompendium.models

import java.net.URI

data class OpenApiSpecInfoLicense(
  var name: String,
  var url: URI? = null
)
