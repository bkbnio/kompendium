package oas.security

import kotlinx.serialization.Serializable

@Serializable
class BasicAuth : SecuritySchema {
  val type: String = "http"
  val scheme: String = "basic"
}
