package io.bkbn.kompendium.auth

import io.bkbn.kompendium.auth.KompendiumAuth.notarizedJwt
import io.bkbn.kompendium.auth.KompendiumAuth.notarizedOAuth
import io.bkbn.kompendium.auth.util.TestData
import io.bkbn.kompendium.auth.util.TestData.OPEN_API_ENDPOINT
import io.bkbn.kompendium.auth.util.configBasicAuth
import io.bkbn.kompendium.auth.util.configJwtAuth
import io.bkbn.kompendium.auth.util.configModule
import io.bkbn.kompendium.auth.util.docs
import io.bkbn.kompendium.auth.util.notarizedAuthenticatedGetModule
import io.bkbn.kompendium.core.Kompendium
import io.bkbn.kompendium.oas.security.OAuth
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldNotBe
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.OAuthServerSettings
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication

class KompendiumAuthTest : DescribeSpec({
  afterEach { Kompendium.resetSchema() }
  describe("Basic Authentication") {
    it("Can create a notarized basic authentication record with all expected information") {
      withTestApplication({
        configModule()
        configBasicAuth()
        docs()
        notarizedAuthenticatedGetModule(TestData.AuthConfigName.Basic)
      }) {
        // arrange
        val expected = TestData.getFileSnapshot("notarized_basic_authenticated_get.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content!! shouldEqualJson expected
        }
      }
    }
  }
  describe("JWT Authentication") {
    it("Can create a notarized jwt authentication record with all expected information") {
      withTestApplication({
        configModule()
        configJwtAuth()
        docs()
        notarizedAuthenticatedGetModule(TestData.AuthConfigName.JWT)
      }) {
        // arrange
        val expected = TestData.getFileSnapshot("notarized_jwt_authenticated_get.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content!! shouldEqualJson expected
        }
      }
    }
    it("Can create a JWT authentication record with a custom scheme") {
      withTestApplication({
        configModule()
        configJwtAuth(bearerFormat = "JWT")
        docs()
        notarizedAuthenticatedGetModule(TestData.AuthConfigName.JWT)
      }) {
        // arrange
        val expected = TestData.getFileSnapshot("notarized_jwt_custom_scheme_authenticated_get.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content!! shouldEqualJson expected
        }
      }
    }
    it("Can create a JWT authentication record with multiple schemas") {
      withTestApplication({
        configModule()
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
        // arrange
        val expected = TestData.getFileSnapshot("notarized_multiple_jwt_authenticated_get.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content!! shouldEqualJson expected
        }
      }
    }
  }
  describe("OAuth Authentication") {
    it("Can create an Oauth schema with all possible flows") {
      // arrange
      val expected = TestData.getFileSnapshot("notarized_oauth_implicit_flow.json").trim()
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
        configModule()
        install(Authentication) {
          notarizedOAuth(flows, "oauth") {
            urlProvider = { "http://localhost:8080/callback" }
            client = HttpClient(CIO)
            providerLookup = {
              OAuthServerSettings.OAuth2ServerSettings(
                name = "google",
                authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
                requestMethod = HttpMethod.Post,
                clientId = System.getenv("GOOGLE_CLIENT_ID"),
                clientSecret = System.getenv("GOOGLE_CLIENT_SECRET"),
                defaultScopes = listOf("https://www.googleapis.com/auth/userinfo.profile")
              )
            }
          }
        }
        docs()
        notarizedAuthenticatedGetModule("oauth")
      }) {
        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content!! shouldEqualJson expected
        }
      }
    }
  }
})
