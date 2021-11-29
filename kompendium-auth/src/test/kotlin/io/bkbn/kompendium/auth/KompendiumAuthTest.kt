package io.bkbn.kompendium.auth

import io.bkbn.kompendium.auth.KompendiumAuth.notarizedJwt
import io.bkbn.kompendium.auth.util.AuthConfigName
import io.bkbn.kompendium.auth.util.configBasicAuth
import io.bkbn.kompendium.auth.util.configJwtAuth
import io.bkbn.kompendium.auth.util.notarizedAuthenticatedGetModule
import io.bkbn.kompendium.auth.util.setupOauth
import io.bkbn.kompendium.core.fixtures.TestHelpers.openApiTest
import io.bkbn.kompendium.oas.security.OAuth
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.application.install
import io.ktor.auth.Authentication

class KompendiumAuthTest : DescribeSpec({
  describe("Basic Authentication") {
    it("Can create a notarized basic authentication record with all expected information") {
      // act
      openApiTest("notarized_basic_authenticated_get.json") {
        configBasicAuth()
        notarizedAuthenticatedGetModule(AuthConfigName.Basic)
      }
    }
  }
  describe("JWT Authentication") {
    it("Can create a notarized jwt authentication record with all expected information") {
      // act
      openApiTest("notarized_jwt_authenticated_get.json") {
        configJwtAuth()
        notarizedAuthenticatedGetModule(AuthConfigName.JWT)
      }
    }
    it("Can create a JWT authentication record with a custom scheme") {
      // act
      openApiTest("notarized_jwt_custom_scheme_authenticated_get.json") {
        configJwtAuth(bearerFormat = "JWT")
        notarizedAuthenticatedGetModule(AuthConfigName.JWT)
      }
    }
    it("Can create a JWT authentication record with multiple schemas") {
      // act
      openApiTest("notarized_multiple_jwt_authenticated_get.json") {
        install(Authentication) {
          notarizedJwt("jwt1") {
            realm = "Ktor server"
          }
          notarizedJwt("jwt2") {
            realm = "Ktor server"
          }
        }
        notarizedAuthenticatedGetModule("jwt1", "jwt2")
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

      // act
      openApiTest("notarized_oauth_implicit_flow.json") {
        setupOauth(flows)
        notarizedAuthenticatedGetModule("oauth")
      }
    }
  }
})
