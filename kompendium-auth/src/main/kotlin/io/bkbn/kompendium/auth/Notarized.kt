package io.bkbn.kompendium.auth

import io.bkbn.kompendium.auth.configuration.ApiKeyConfiguration
import io.bkbn.kompendium.auth.configuration.BasicAuthConfiguration
import io.bkbn.kompendium.auth.configuration.JwtAuthConfiguration
import io.bkbn.kompendium.auth.configuration.OAuthConfiguration
import io.bkbn.kompendium.auth.configuration.SecurityConfiguration
import io.bkbn.kompendium.core.Kompendium
import io.bkbn.kompendium.oas.security.ApiKeyAuth
import io.bkbn.kompendium.oas.security.BasicAuth
import io.bkbn.kompendium.oas.security.BearerAuth
import io.bkbn.kompendium.oas.security.OAuth
import io.ktor.application.feature
import io.ktor.auth.authenticate
import io.ktor.routing.Route
import io.ktor.routing.application

object Notarized {

  fun Route.notarizedAuthenticate(
    vararg configurations: SecurityConfiguration,
    optional: Boolean = false,
    build: Route.() -> Unit
  ): Route {
    val configurationNames = configurations.map { it.name }.toTypedArray()
    val feature = application.feature(Kompendium)

    configurations.forEach { config ->
      feature.config.spec.components.securitySchemes[config.name] = when (config) {
        is ApiKeyConfiguration -> ApiKeyAuth(config.location, config.keyName)
        is BasicAuthConfiguration -> BasicAuth()
        is JwtAuthConfiguration -> BearerAuth(config.bearerFormat)
        is OAuthConfiguration -> OAuth(config.description, config.flows)
      }
    }

    return authenticate(*configurationNames, optional = optional, build = build)
  }

}
