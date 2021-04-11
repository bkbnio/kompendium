package org.leafygreens.kompendium.models

import java.net.URI

data class OpenApiSpecInfoContact(
  val name: String,
  val url: URI? = null,
  val email: String? = null // TODO Enforce email?
)
