package oas.info

import java.net.URI

data class Contact(
  var name: String,
  var url: URI? = null,
  var email: String? = null // TODO Enforce email?
)
