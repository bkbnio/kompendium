package io.bkbn.kompendium.auth

import io.ktor.auth.Authentication
import io.ktor.auth.basic
import io.ktor.auth.BasicAuthenticationProvider
import io.ktor.auth.jwt.jwt
import io.ktor.auth.jwt.JWTAuthenticationProvider
import io.bkbn.kompendium.core.Kompendium
import io.bkbn.kompendium.oas.security.ApiKeyAuth
import io.bkbn.kompendium.oas.security.BasicAuth
import io.bkbn.kompendium.oas.security.BearerAuth
import io.bkbn.kompendium.oas.security.OAuth
import io.ktor.auth.AuthenticationRouteSelector
import io.ktor.auth.OAuthAuthenticationProvider
import io.ktor.auth.oauth

object KompendiumAuth {

  private const val DEFAULT_NAME = "default"

  init {
    Kompendium.addCustomRouteHandler(AuthenticationRouteSelector::class) { route, tail ->
      calculate(route.parent, tail)
    }
  }

  fun Authentication.Configuration.notarizedBasic(
    name: String? = null,
    configure: BasicAuthenticationProvider.Configuration.() -> Unit
  ) {
    Kompendium.openApiSpec.components.securitySchemes[name ?: DEFAULT_NAME] = BasicAuth()
    basic(name, configure)
  }

  fun Authentication.Configuration.notarizedJwt(
    name: String? = null,
    bearerFormat: String? = null,
    configure: JWTAuthenticationProvider.Configuration.() -> Unit
  ) {
    Kompendium.openApiSpec.components.securitySchemes[name ?: DEFAULT_NAME] = BearerAuth(bearerFormat)
    jwt(name, configure)
  }

  fun Authentication.Configuration.notarizedApiKey(
    location: ApiKeyAuth.ApiKeyLocation,
    keyName: String,
    authName: String? = null,
  ) {
    Kompendium.openApiSpec.components.securitySchemes[authName ?: DEFAULT_NAME] = ApiKeyAuth(location, keyName)
  }

  fun Authentication.Configuration.notarizedOAuth(
    flows: OAuth.Flows,
    name: String? = null,
    description: String? = null,
    configure: OAuthAuthenticationProvider.Configuration.() -> Unit
  ) {
    Kompendium.openApiSpec.components.securitySchemes[name ?: DEFAULT_NAME] = OAuth(description, flows)
    oauth(name, configure)
  }
}
