package io.bkbn.kompendium.oas.security

import kotlinx.serialization.Serializable
import java.util.Locale

// TODO... is there even an official ktor api auth mechanism??

@Serializable
@Suppress("UnusedPrivateMember")
class ApiKeyAuth(val `in`: ApiKeyLocation, val name: String) : SecuritySchema {
  val type: String = "apiKey"

  enum class ApiKeyLocation {
    HEADER,
    QUERY,
    COOKIE;

    override fun toString(): String = name.lowercase(Locale.getDefault())
  }
}
