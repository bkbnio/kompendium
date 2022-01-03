package io.bkbn.kompendium.auth.configuration

import io.bkbn.kompendium.oas.security.OAuth

interface OAuthConfiguration: SecurityConfiguration {
  val flows: OAuth.Flows
  val description: String?
    get() = null
}
