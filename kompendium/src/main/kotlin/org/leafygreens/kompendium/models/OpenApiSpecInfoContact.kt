package org.leafygreens.kompendium.models

import java.net.URI

data class OpenApiSpecInfoContact(
  val name: String,
  val url: URI?,
  val email: String? // TODO Enforce email
)
