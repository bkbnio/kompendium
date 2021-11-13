package io.bkbn.kompendium.oas.info

import java.net.URI

data class License(
  var name: String,
  var url: URI? = null
)
