package io.bkbn.kompendium.oas.security

class BasicAuth : SecuritySchema {
  val type: String = "http"
  val scheme: String = "basic"
}
