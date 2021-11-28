package io.bkbn.kompendium.auth

import io.bkbn.kompendium.auth.KompendiumAuth.notarizedJwt
import io.bkbn.kompendium.auth.util.AuthConfigName
import io.bkbn.kompendium.auth.util.configBasicAuth
import io.bkbn.kompendium.auth.util.configJwtAuth
import io.bkbn.kompendium.auth.util.notarizedAuthenticatedGetModule
import io.bkbn.kompendium.auth.util.setupOauth
import io.bkbn.kompendium.core.Kompendium
import io.bkbn.kompendium.core.TestHelpers.compareOpenAPISpec
import io.bkbn.kompendium.core.docs
import io.bkbn.kompendium.core.jacksonConfigModule
import io.bkbn.kompendium.oas.security.OAuth
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.server.testing.withTestApplication

class KompendiumAuthTest : DescribeSpec({
  afterEach { Kompendium.resetSchema() }
  describe("Basic Authentication") {
    it("Can create a notarized basic authentication record with all expected information") {
      // arrange
      withTestApplication({
        jacksonConfigModule()
        docs()
        configBasicAuth()
        notarizedAuthenticatedGetModule(AuthConfigName.Basic)
      }) {
        // act
        compareOpenAPISpec("notarized_basic_authenticated_get.json")
      }
    }
  }
  describe("JWT Authentication") {
    it("Can create a notarized jwt authentication record with all expected information") {
      // arrange
      withTestApplication({
        jacksonConfigModule()
        configJwtAuth()
        docs()
        notarizedAuthenticatedGetModule(AuthConfigName.JWT)
      }) {
        // act
        compareOpenAPISpec("notarized_jwt_authenticated_get.json")
      }
    }
    it("Can create a JWT authentication record with a custom scheme") {
      // arrange
      withTestApplication({
        jacksonConfigModule()
        configJwtAuth(bearerFormat = "JWT")
        docs()
        notarizedAuthenticatedGetModule(AuthConfigName.JWT)
      }) {
        // act
        compareOpenAPISpec("notarized_jwt_custom_scheme_authenticated_get.json")
      }
    }
    it("Can create a JWT authentication record with multiple schemas") {
      // arrange
      withTestApplication({
        jacksonConfigModule()
        install(Authentication) {
          notarizedJwt("jwt1") {
            realm = "Ktor server"
          }
          notarizedJwt("jwt2") {
            realm = "Ktor server"
          }
        }
        docs()
        notarizedAuthenticatedGetModule("jwt1", "jwt2")
      }) {
        // act
        compareOpenAPISpec("notarized_multiple_jwt_authenticated_get.json")
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

      withTestApplication({
        jacksonConfigModule()
        setupOauth(flows)
        docs()
        notarizedAuthenticatedGetModule("oauth")
      }) {
        // act
        compareOpenAPISpec("notarized_oauth_implicit_flow.json")
      }
    }
  }
})
