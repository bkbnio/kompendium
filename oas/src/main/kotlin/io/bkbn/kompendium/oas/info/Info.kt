package io.bkbn.kompendium.oas.info

import io.bkbn.kompendium.oas.serialization.UriSerializer
import kotlinx.serialization.Serializable
import java.net.URI

@Serializable
data class Info(
  var title: String? = null,
  var version: String? = null,
  var description: String? = null,
  @Serializable(with = UriSerializer::class)
  var termsOfService: URI? = null,
  var contact: Contact? = null,
  var license: License? = null
)
