package io.bkbn.kompendium.oas.info

import java.net.URI

data class Info(
  var title: String? = null,
  var version: String? = null,
  var description: String? = null,
  var termsOfService: URI? = null,
  var contact: Contact? = null,
  var license: License? = null
)
