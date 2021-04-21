package org.leafygreens.kompendium.auth

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import org.junit.Test
import org.leafygreens.kompendium.Kompendium
import org.leafygreens.kompendium.Kompendium.notarizedGet
import org.leafygreens.kompendium.auth.KompendiumAuth.notarizedBasic
import org.leafygreens.kompendium.auth.KompendiumAuth.notarizedJwt
import org.leafygreens.kompendium.auth.util.TestData
import org.leafygreens.kompendium.auth.util.TestParams
import org.leafygreens.kompendium.auth.util.TestResponse
import org.leafygreens.kompendium.models.meta.MethodInfo
import org.leafygreens.kompendium.models.meta.ResponseInfo
import org.leafygreens.kompendium.models.oas.OpenApiSpec
import org.leafygreens.kompendium.models.oas.OpenApiSpecInfo
import org.leafygreens.kompendium.routes.openApi
import org.leafygreens.kompendium.routes.redoc
import org.leafygreens.kompendium.util.KompendiumHttpCodes
import kotlin.test.AfterTest
import kotlin.test.assertEquals

internal class KompendiumAuthTest {

  @AfterTest
  fun `reset kompendium`() {
    Kompendium.openApiSpec = OpenApiSpec(
      info = OpenApiSpecInfo(),
      servers = mutableListOf(),
      paths = mutableMapOf()
    )
    Kompendium.cache = emptyMap()
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
          notarizedGet<TestParams, TestResponse>(testGetInfo(*authenticationConfigName)) {
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
    val testGetResponse = ResponseInfo(KompendiumHttpCodes.OK, "A Successful Endeavor")
    fun testGetInfo(vararg security: String) =
      MethodInfo("Another get test", "testing more", testGetResponse, securitySchemes = security.toSet())
  }
}
