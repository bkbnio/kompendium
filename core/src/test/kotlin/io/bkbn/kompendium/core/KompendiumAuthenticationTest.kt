package io.bkbn.kompendium.core

import dev.forst.ktor.apikey.apiKey
import io.bkbn.kompendium.core.fixtures.TestHelpers
import io.bkbn.kompendium.core.util.customAuthConfig
import io.bkbn.kompendium.core.util.customScopesOnSiblingPathOperations
import io.bkbn.kompendium.core.util.defaultAuthConfig
import io.bkbn.kompendium.core.util.multipleAuthStrategies
import io.bkbn.kompendium.oas.component.Components
import io.bkbn.kompendium.oas.security.ApiKeyAuth
import io.bkbn.kompendium.oas.security.BasicAuth
import io.bkbn.kompendium.oas.security.BearerAuth
import io.bkbn.kompendium.oas.security.OAuth
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.http.HttpMethod
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.OAuthServerSettings
import io.ktor.server.auth.Principal
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.auth.oauth

class KompendiumAuthenticationTest : DescribeSpec({
  describe("Authentication") {
    it("Can add a default auth config by default") {
      TestHelpers.openApiTestAllSerializers(
        snapshotName = "T0045__default_auth_config.json",
        applicationSetup = {
          install(Authentication) {
            basic("basic") {
              realm = "Ktor Server"
              validate { UserIdPrincipal("Placeholder") }
            }
          }
        },
        specOverrides = {
          this.copy(
            components = Components(
              securitySchemes = mutableMapOf(
                "basic" to BasicAuth()
              )
            )
          )
        }
      ) { defaultAuthConfig() }
    }
    it("Can provide custom auth config with proper scopes") {
      TestHelpers.openApiTestAllSerializers(
        snapshotName = "T0046__custom_auth_config.json",
        applicationSetup = {
          install(Authentication) {
            oauth("auth-oauth-google") {
              urlProvider = { "http://localhost:8080/callback" }
              providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                  name = "google",
                  authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                  accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
                  requestMethod = HttpMethod.Post,
                  clientId = "DUMMY_VAL",
                  clientSecret = "DUMMY_VAL",
                  defaultScopes = listOf("https://www.googleapis.com/auth/userinfo.profile"),
                  extraTokenParameters = listOf("access_type" to "offline")
                )
              }
              client = HttpClient(CIO)
            }
          }
        },
        specOverrides = {
          this.copy(
            components = Components(
              securitySchemes = mutableMapOf(
                "auth-oauth-google" to OAuth(
                  flows = OAuth.Flows(
                    implicit = OAuth.Flows.Implicit(
                      authorizationUrl = "https://accounts.google.com/o/oauth2/auth",
                      scopes = mapOf(
                        "write:pets" to "modify pets in your account",
                        "read:pets" to "read your pets"
                      )
                    )
                  )
                )
              )
            )
          )
        }
      ) { customAuthConfig() }
    }
    it("Can provide multiple authentication strategies") {
      data class TestAppPrincipal(val key: String) : Principal
      TestHelpers.openApiTestAllSerializers(
        snapshotName = "T0047__multiple_auth_strategies.json",
        applicationSetup = {
          install(Authentication) {
            apiKey("api-key") {
              headerName = "X-API-KEY"
              validate { key ->
                // api key library (dev.forst.ktor.apikey) is using the deprecated `Principal` class
                key
                  .takeIf { it == "api-key" }
                  ?.let { TestAppPrincipal(it) }
              }
            }
            jwt("jwt") {
              realm = "Server"
            }
          }
        },
        specOverrides = {
          this.copy(
            components = Components(
              securitySchemes = mutableMapOf(
                "jwt" to BearerAuth("JWT"),
                "api-key" to ApiKeyAuth(ApiKeyAuth.ApiKeyLocation.HEADER, "X-API-KEY")
              )
            )
          )
        }
      ) { multipleAuthStrategies() }
    }
    it("Can provide different scopes on path operations in the same route") {
      TestHelpers.openApiTestAllSerializers(
        snapshotName = "T0074__auth_on_specific_path_operation.json",
        applicationSetup = {
          install(Authentication) {
            oauth("auth-oauth-google") {
              urlProvider = { "http://localhost:8080/callback" }
              providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                  name = "google",
                  authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                  accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
                  requestMethod = HttpMethod.Post,
                  clientId = "DUMMY_VAL",
                  clientSecret = "DUMMY_VAL",
                  defaultScopes = listOf("https://www.googleapis.com/auth/userinfo.profile"),
                  extraTokenParameters = listOf("access_type" to "offline")
                )
              }
              client = HttpClient(CIO)
            }
          }
        },
        specOverrides = {
          this.copy(
            components = Components(
              securitySchemes = mutableMapOf(
                "auth-oauth-google" to OAuth(
                  flows = OAuth.Flows(
                    implicit = OAuth.Flows.Implicit(
                      authorizationUrl = "https://accounts.google.com/o/oauth2/auth",
                      scopes = mapOf(
                        "write:pets" to "modify pets in your account",
                        "read:pets" to "read your pets"
                      )
                    )
                  )
                )
              )
            )
          )
        }
      ) { customScopesOnSiblingPathOperations() }
    }
  }
})
