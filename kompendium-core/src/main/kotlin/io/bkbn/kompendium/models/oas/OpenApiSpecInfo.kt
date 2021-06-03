package io.bkbn.kompendium.models.oas

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.net.URI



@Serializable
data class OpenApiSpecInfo(
  var title: String? = null,
  var version: String? = null,
  var description: String? = null,
  @Contextual
  var termsOfService: URI? = null,
  var contact: OpenApiSpecInfoContact? = null,
  var license: OpenApiSpecInfoLicense? = null
)
