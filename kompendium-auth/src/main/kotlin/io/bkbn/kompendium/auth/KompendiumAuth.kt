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

/**
 * This object is responsible for initializing all necessary auth route handlers, as well as providing wrapper methods
 * that can be used in place of the standard ktor-auth configuration blocks.
 */
object KompendiumAuth {

  private const val DEFAULT_NAME = "default"

  init {
    Kompendium.addCustomRouteHandler(AuthenticationRouteSelector::class) { route, tail ->
      calculate(route.parent, tail)
    }
  }

  /**
   * Adds a basic auth schema to the [Kompendium] security schemas list with the specified name
   * @param name Optional name to be assigned to this security schema.  If not provided, will be labelled as "default"
   * @param configure Configuration block that will be passed to Ktor.
   */
  fun Authentication.Configuration.notarizedBasic(
    name: String? = null,
    configure: BasicAuthenticationProvider.Configuration.() -> Unit
  ) {
    Kompendium.openApiSpec.components.securitySchemes[name ?: DEFAULT_NAME] = BasicAuth()
    basic(name, configure)
  }

  /**
   * Adds a bearer format schema to the [Kompendium] security schemas list with the specified name
   * @param name Optional name to be assigned to this security schema.  If not provided, will be labelled as "default"
   * @param bearerFormat Optional string used to provide end users with insight into
   * the expected format of the bearer token.  Defaults to "JWT"
   * @param configure Configuration block that will be passed to Ktor.
   */
  fun Authentication.Configuration.notarizedJwt(
    name: String? = null,
    bearerFormat: String? = "JWT",
    configure: JWTAuthenticationProvider.Configuration.() -> Unit
  ) {
    Kompendium.openApiSpec.components.securitySchemes[name ?: DEFAULT_NAME] = BearerAuth(bearerFormat)
    jwt(name, configure)
  }

  /**
   * Adds an API key schema to the [Kompendium] security schemas list with the specified name
   * @param location Expected location of the API key.  Must be of type [ApiKeyAuth.ApiKeyLocation]
   * @param keyName Name of the API key.
   * @param authName Optional name to be assigned to this security schema.
   * If not provided, will be labelled as "default"
   */
  fun Authentication.Configuration.notarizedApiKey(
    location: ApiKeyAuth.ApiKeyLocation,
    keyName: String,
    authName: String? = null,
  ) {
    Kompendium.openApiSpec.components.securitySchemes[authName ?: DEFAULT_NAME] = ApiKeyAuth(location, keyName)
  }

  /**
   * Adds OAuth schema to the [Kompendium] security schemas list with the specified name and flows
   * @param flows The flows available to authenticate with the specified OAuth provider.
   * @param name Optional name to be assigned to this security schema.  If not provided, will be labelled as "default"
   * @param description Optional description to be used to provide more insight into the OAuth mechanism
   * @param configure Configuration block that will be passed to Ktor.
   */
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
