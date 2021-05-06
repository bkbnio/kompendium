package org.leafygreens.kompendium.auth

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
import org.leafygreens.kompendium.Kompendium
import org.leafygreens.kompendium.Notarized.notarizedGet
import org.leafygreens.kompendium.auth.KompendiumAuth.notarizedBasic
import org.leafygreens.kompendium.auth.KompendiumAuth.notarizedJwt
import org.leafygreens.kompendium.auth.util.TestData
import org.leafygreens.kompendium.auth.util.TestParams
import org.leafygreens.kompendium.auth.util.TestResponse
import org.leafygreens.kompendium.models.meta.MethodInfo
import org.leafygreens.kompendium.models.meta.ResponseInfo
import org.leafygreens.kompendium.routes.openApi
import org.leafygreens.kompendium.routes.redoc

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
      configJwtAuth(scheme = "oauth")
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
  fun `Notarized Get with jwt authentication and custom header records all expected information`() {
    withTestApplication({
      configModule()
      configJwtAuth(header = "x-api-key")
      docs()
      notarizedAuthenticatedGetModule(TestData.AuthConfigName.JWT)
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = TestData.getFileSnapshot("notarized_jwt_custom_header_authenticated_get.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Notarized Get with multiple jwt schemes records all expected information`() {
    withTestApplication({
      configModule()
      install(Authentication) {
        notarizedJwt("jwt1", header = "x-api-key-1") {
          realm = "Ktor server"
        }
        notarizedJwt("jwt2", header = "x-api-key-2") {
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
    header: String? = null,
    scheme: String? = null
  ) {
    install(Authentication) {
      notarizedJwt(TestData.AuthConfigName.JWT, header, scheme) {
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
