package io.bkbn.kompendium.oas.info

import io.bkbn.kompendium.oas.serialization.UriSerializer
import kotlinx.serialization.Serializable
import java.net.URI

@Serializable
data class Contact(
  var name: String,
  @Serializable(with = UriSerializer::class)
  var url: URI? = null,
  var email: String? = null // TODO Enforce email?
)
