package oas.security

import kotlinx.serialization.Serializable

@Serializable
data class BearerAuth(val bearerFormat: String? = null): SecuritySchema {
  val type: String = "http"
  val scheme: String = "bearer"
}
