package io.bkbn.kompendium.models.oas

import java.net.URI

data class OpenApiSpecInfoContact(
  var name: String,
  var url: URI? = null,
  var email: String? = null // TODO Enforce email?
)
