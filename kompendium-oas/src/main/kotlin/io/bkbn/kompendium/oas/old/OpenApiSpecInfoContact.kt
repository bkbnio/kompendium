package io.bkbn.kompendium.oas.old

import java.net.URI

data class OpenApiSpecInfoContact(
  var name: String,
  var url: URI? = null,
  var email: String? = null // TODO Enforce email?
)
