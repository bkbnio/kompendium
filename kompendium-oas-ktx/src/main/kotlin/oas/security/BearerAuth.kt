package oas.security

data class BearerAuth(val bearerFormat: String? = null): SecuritySchema {
  val type: String = "http"
  val scheme: String = "bearer"
}
