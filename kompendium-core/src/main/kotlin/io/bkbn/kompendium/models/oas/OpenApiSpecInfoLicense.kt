package io.bkbn.kompendium.models.oas

import java.net.URI

data class OpenApiSpecInfoLicense(
  var name: String,
  var url: URI? = null
)
