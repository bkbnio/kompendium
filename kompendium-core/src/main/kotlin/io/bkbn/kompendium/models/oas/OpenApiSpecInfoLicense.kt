package io.bkbn.kompendium.models.oas

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.net.URI

@Serializable
data class OpenApiSpecInfoLicense(
  var name: String,
  @Contextual
  var url: URI? = null
)
