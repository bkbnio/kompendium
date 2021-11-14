package io.bkbn.kompendium.oas.security

import java.util.Locale

// TODO... is there even an official ktor api auth mechanism??

@Suppress("UnusedPrivateMember")
class ApiKeyAuth(val `in`: ApiKeyLocation, name: String) : SecuritySchema {
  val type: String = "apiKey"

  enum class ApiKeyLocation {
    HEADER,
    QUERY,
    COOKIE;

    override fun toString(): String = name.lowercase(Locale.getDefault())
  }
}
