package io.bkbn.kompendium.oas.security

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Locale

// TODO... is there even an official ktor api auth mechanism??

@Serializable
@Suppress("UnusedPrivateMember")
class ApiKeyAuth(val `in`: ApiKeyLocation, val name: String) : SecuritySchema {
  val type: String = "apiKey"

  @Serializable
  enum class ApiKeyLocation {
    @SerialName("header")
    HEADER,

    @SerialName("query")
    QUERY,

    @SerialName("cookie")
    COOKIE;

    override fun toString(): String = name.lowercase(Locale.getDefault())
  }
}
