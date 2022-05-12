package io.bkbn.kompendium.auth.configuration

import io.bkbn.kompendium.oas.security.ApiKeyAuth

interface ApiKeyConfiguration : SecurityConfiguration {
  val location: ApiKeyAuth.ApiKeyLocation
  val keyName: String
}
