package io.bkbn.kompendium.auth

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respondText
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import kotlin.test.AfterTest
import kotlin.test.assertEquals
import org.junit.Test
import io.bkbn.kompendium.core.Kompendium
import io.bkbn.kompendium.core.Notarized.notarizedGet
import io.bkbn.kompendium.auth.KompendiumAuth.notarizedBasic
import io.bkbn.kompendium.auth.KompendiumAuth.notarizedJwt
import io.bkbn.kompendium.auth.KompendiumAuth.notarizedOAuth
import io.bkbn.kompendium.auth.util.TestData
import io.bkbn.kompendium.auth.util.TestParams
import io.bkbn.kompendium.auth.util.TestResponse
import io.bkbn.kompendium.core.metadata.MethodInfo
import io.bkbn.kompendium.core.metadata.ResponseInfo
import io.bkbn.kompendium.core.routes.openApi
import io.bkbn.kompendium.core.routes.redoc
import io.bkbn.kompendium.oas.security.OAuth
import io.ktor.auth.OAuthServerSettings
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO

internal class KompendiumAuthTest {

  @AfterTest
  fun `reset kompendium`() {
    Kompendium.resetSchema()
  }

  @Test
  fun `Notarized Get with basic authentication records all expected information`() {
    withTestApplication({
      configModule()
      configBasicAuth()
      docs()
      notarizedAuthenticatedGetModule(TestData.AuthConfigName.Basic)
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = TestData.getFileSnapshot("notarized_basic_authenticated_get.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Notarized Get with jwt authentication records all expected information`() {
    withTestApplication({
      configModule()
      configJwtAuth()
      docs()
      notarizedAuthenticatedGetModule(TestData.AuthConfigName.JWT)
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = TestData.getFileSnapshot("notarized_jwt_authenticated_get.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Notarized Get with jwt authentication and custom scheme records all expected information`() {
    withTestApplication({
      configModule()
      configJwtAuth(bearerFormat = "JWT")
      docs()
      notarizedAuthenticatedGetModule(TestData.AuthConfigName.JWT)
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = TestData.getFileSnapshot("notarized_jwt_custom_scheme_authenticated_get.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Notarized Get with multiple jwt schemes records all expected information`() {
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
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = TestData.getFileSnapshot("notarized_multiple_jwt_authenticated_get.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Notarized Oauth with all flows`() {
    val flows = OAuth.Flows(
      implicit = OAuth.Flows.Implicit("https://accounts.google.com/o/oauth2/auth", scopes = mapOf("test" to "is a cool scope", "this" to "is also cool")),
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
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = TestData.getFileSnapshot("notarized_oauth_implicit_flow.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  private fun Application.configModule() {
    install(ContentNegotiation) {
      jackson(ContentType.Application.Json) {
        enable(SerializationFeature.INDENT_OUTPUT)
        setSerializationInclusion(JsonInclude.Include.NON_NULL)
      }
    }
  }

  private fun Application.configBasicAuth() {
    install(Authentication) {
      notarizedBasic(TestData.AuthConfigName.Basic) {
        realm = "Ktor Server"
        validate { credentials ->
          if (credentials.name == credentials.password) {
            UserIdPrincipal(credentials.name)
          } else {
            null
          }
        }
      }
    }
  }

  private fun Application.configJwtAuth(
    bearerFormat: String? = null
  ) {
    install(Authentication) {
      notarizedJwt(TestData.AuthConfigName.JWT, bearerFormat) {
        realm = "Ktor server"
      }
    }
  }

  private fun Application.notarizedAuthenticatedGetModule(vararg authenticationConfigName: String) {
    routing {
      authenticate(*authenticationConfigName) {
        route(TestData.getRoutePath) {
          notarizedGet(testGetInfo(*authenticationConfigName)) {
            call.respondText { "hey dude ‼️ congratz on the get request" }
          }
        }
      }
    }
  }

  private val oas = Kompendium.openApiSpec.copy()

  private fun Application.docs() {
    routing {
      openApi(oas)
      redoc(oas)
    }
  }

  private companion object {
    val testGetResponse = ResponseInfo<TestResponse>(HttpStatusCode.OK, "A Successful Endeavor")
    fun testGetInfo(vararg security: String) =
      MethodInfo.GetInfo<TestParams, TestResponse>(
        summary = "Another get test",
        description = "testing more",
        responseInfo = testGetResponse,
        securitySchemes = security.toSet()
      )
  }
}
