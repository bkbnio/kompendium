package io.bkbn.kompendium.auth

import io.bkbn.kompendium.auth.Notarized.notarizedAuthenticate
import io.bkbn.kompendium.auth.configuration.BasicAuthConfiguration
import io.bkbn.kompendium.auth.configuration.JwtAuthConfiguration
import io.bkbn.kompendium.auth.configuration.OAuthConfiguration
import io.bkbn.kompendium.auth.util.AuthConfigName
import io.bkbn.kompendium.auth.util.configBasicAuth
import io.bkbn.kompendium.auth.util.configJwtAuth
import io.bkbn.kompendium.auth.util.setupOauth
import io.bkbn.kompendium.auth.util.testGetInfo
import io.bkbn.kompendium.core.Notarized.notarizedGet
import io.bkbn.kompendium.core.fixtures.TestHelpers.openApiTest
import io.bkbn.kompendium.oas.security.OAuth
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.route
import io.ktor.routing.routing

class KompendiumAuthTest : DescribeSpec({
  describe("Basic Authentication") {
    it("Can create a notarized basic authentication record with all expected information") {
      // arrange
      val authConfig = object : BasicAuthConfiguration {
        override val name: String = AuthConfigName.Basic
      }

      // act
      openApiTest("notarized_basic_authenticated_get.json") {
        configBasicAuth()
        routing {
          notarizedAuthenticate(authConfig) {
            route("/test") {
              notarizedGet(testGetInfo(AuthConfigName.Basic)) {
                call.respondText { "hey dude ‼️ congratz on the get request" }
              }
            }
          }
        }
      }
    }
  }
  describe("JWT Authentication") {
    it("Can create a simple notarized JWT route") {
      // arrange
      val authConfig = object : JwtAuthConfiguration {
        override val name: String = AuthConfigName.JWT
      }

      // act
      openApiTest("notarized_jwt_authenticated_get.json") {
        configJwtAuth()
        routing {
          notarizedAuthenticate(authConfig) {
            route("/test") {
              notarizedGet(testGetInfo(authConfig.name)) {
                call.respondText { "hey dude ‼️ congratz on the get request" }
              }
            }
          }
        }
      }
    }
  }
  describe("OAuth Authentication") {
    it("Can create an Oauth schema with all possible flows") {
      // arrange
      val flows = OAuth.Flows(
        implicit = OAuth.Flows.Implicit(
          "https://accounts.google.com/o/oauth2/auth",
          scopes = mapOf("test" to "is a cool scope", "this" to "is also cool")
        ),
        authorizationCode = OAuth.Flows.AuthorizationCode("https://accounts.google.com/o/oauth2/auth"),
        password = OAuth.Flows.Password("https://accounts.google.com/o/oauth2/auth"),
        clientCredentials = OAuth.Flows.ClientCredential("https://accounts.google.com/token")
      )

      val authConfig = object : OAuthConfiguration {
        override val flows: OAuth.Flows = flows
        override val name: String = AuthConfigName.OAuth
      }

      // act
      openApiTest("notarized_oauth_all_flows.json") {
        setupOauth()
        routing {
          notarizedAuthenticate(authConfig) {
            route("/test") {
              notarizedGet(testGetInfo(authConfig.name)) {
                call.respondText { "hey dude ‼️ congratz on the get request" }
              }
            }
          }
        }
      }
    }
  }
})
