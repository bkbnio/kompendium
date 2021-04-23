package org.leafygreens.kompendium

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import java.net.URI
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.leafygreens.kompendium.Kompendium.notarizedDelete
import org.leafygreens.kompendium.Kompendium.notarizedGet
import org.leafygreens.kompendium.Kompendium.notarizedPost
import org.leafygreens.kompendium.Kompendium.notarizedPut
import org.leafygreens.kompendium.annotations.QueryParam
import org.leafygreens.kompendium.models.meta.MethodInfo
import org.leafygreens.kompendium.models.meta.RequestInfo
import org.leafygreens.kompendium.models.meta.ResponseInfo
import org.leafygreens.kompendium.models.oas.OpenApiSpecInfo
import org.leafygreens.kompendium.models.oas.OpenApiSpecInfoContact
import org.leafygreens.kompendium.models.oas.OpenApiSpecInfoLicense
import org.leafygreens.kompendium.models.oas.OpenApiSpecServer
import org.leafygreens.kompendium.routes.openApi
import org.leafygreens.kompendium.routes.redoc
import org.leafygreens.kompendium.util.ComplexRequest
import org.leafygreens.kompendium.util.KompendiumHttpCodes
import org.leafygreens.kompendium.util.TestCreatedResponse
import org.leafygreens.kompendium.util.TestData
import org.leafygreens.kompendium.util.TestParams
import org.leafygreens.kompendium.util.TestRequest
import org.leafygreens.kompendium.util.TestResponse

internal class KompendiumTest {

  @AfterTest
  fun `reset kompendium`() {
    Kompendium.resetSchema()
  }

  @Test
  fun `Kompendium can be instantiated with no details`() {
    assertEquals(Kompendium.openApiSpec.openapi, "3.0.3", "Kompendium has a default spec version of 3.0.3")
  }

  @Test
  fun `Notarized Get records all expected information`() {
    withTestApplication({
      configModule()
      docs()
      notarizedGetModule()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = TestData.getFileSnapshot("notarized_get.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Notarized Get does not interrupt the pipeline`() {
    withTestApplication({
      configModule()
      docs()
      notarizedGetModule()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/test").response.content

      // expect
      val expected = "hey dude ‼️ congratz on the get request"
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Notarized Post records all expected information`() {
    withTestApplication({
      configModule()
      docs()
      notarizedPostModule()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = TestData.getFileSnapshot("notarized_post.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Notarized post does not interrupt the pipeline`() {
    withTestApplication({
      configModule()
      docs()
      notarizedPostModule()
    }) {
      // do
      val json = handleRequest(HttpMethod.Post, "/test").response.content

      // expect
      val expected = "hey dude ✌️ congratz on the post request"
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Notarized Put records all expected information`() {
    withTestApplication({
      configModule()
      docs()
      notarizedPutModule()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = TestData.getFileSnapshot("notarized_put.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }


  @Test
  fun `Notarized put does not interrupt the pipeline`() {
    withTestApplication({
      configModule()
      docs()
      notarizedPutModule()
    }) {
      // do
      val json = handleRequest(HttpMethod.Put, "/test").response.content

      // expect
      val expected = "hey pal 🌝 whatcha doin' here?"
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Notarized delete records all expected information`() {
    withTestApplication({
      configModule()
      docs()
      notarizedDeleteModule()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = TestData.getFileSnapshot("notarized_delete.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Notarized delete does not interrupt the pipeline`() {
    withTestApplication({
      configModule()
      docs()
      notarizedDeleteModule()
    }) {
      // do
      val status = handleRequest(HttpMethod.Delete, "/test").response.status()

      // expect
      assertEquals(HttpStatusCode.NoContent, status, "No content status should be received")
    }
  }

  @Test
  fun `Path parser stores the expected path`() {
    withTestApplication({
      configModule()
      docs()
      pathParsingTestModule()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = TestData.getFileSnapshot("path_parser.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Can notarize the root route`() {
    withTestApplication({
      configModule()
      docs()
      rootModule()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = TestData.getFileSnapshot("root_route.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Can call the root route`() {
    withTestApplication({
      configModule()
      docs()
      rootModule()
    }) {
      // do
      val result = handleRequest(HttpMethod.Get, "/").response.content

      // expect
      val expected = "☎️🏠🌲"
      assertEquals(expected, result, "Should be the same")
    }
  }

  @Test
  fun `Nested under root module does not append trailing slash`() {
    withTestApplication({
      configModule()
      docs()
      nestedUnderRootModule()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = TestData.getFileSnapshot("nested_under_root.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Can notarize a trailing slash route`() {
    withTestApplication({
      configModule()
      docs()
      trailingSlash()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = TestData.getFileSnapshot("trailing_slash.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Can call a trailing slash route`() {
    withTestApplication({
      configModule()
      docs()
      trailingSlash()
    }) {
      // do
      val result = handleRequest(HttpMethod.Get, "/test/").response.content

      // expect
      val expected = "🙀👾"
      assertEquals(expected, result, "Should be the same")
    }
  }

  @Test
  fun `Can notarize a complex type`() {
    withTestApplication({
      configModule()
      docs()
      complexType()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = TestData.getFileSnapshot("complex_type.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Can notarize primitives`() {
    withTestApplication({
      configModule()
      docs()
      primitives()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = TestData.getFileSnapshot("notarized_primitives.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Can notarize a top level list response`() {
    withTestApplication({
      configModule()
      docs()
      returnsList()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = TestData.getFileSnapshot("response_list.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Can notarize route with no request params and no response body`() {
    withTestApplication({
      configModule()
      docs()
      emptyGet()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = TestData.getFileSnapshot("no_request_params_and_no_response_body.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Can notarize route with non-required params`() {
    withTestApplication({
      configModule()
      docs()
      nonRequiredParamsGet()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = TestData.getFileSnapshot("non_required_params.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Generates the expected redoc`() {
    withTestApplication({
      configModule()
      docs()
      returnsList()
    }) {

      // do
      val html = handleRequest(HttpMethod.Get, "/docs").response.content

      // expected
      val expected = TestData.getFileSnapshot("redoc.html")
      assertEquals(expected, html)
    }
  }

  private companion object {
    val testGetResponse = ResponseInfo(KompendiumHttpCodes.OK, "A Successful Endeavor")
    val testPostResponse = ResponseInfo(KompendiumHttpCodes.CREATED, "A Successful Endeavor")
    val testDeleteResponse =
      ResponseInfo(KompendiumHttpCodes.NO_CONTENT, "A Successful Endeavor", mediaTypes = emptyList())
    val testRequest = RequestInfo("A Test request")
    val testGetInfo = MethodInfo("Another get test", "testing more", testGetResponse)
    val testPostInfo = MethodInfo("Test post endpoint", "Post your tests here!", testPostResponse, testRequest)
    val testPutInfo = MethodInfo("Test put endpoint", "Put your tests here!", testPostResponse, testRequest)
    val testDeleteInfo = MethodInfo("Test delete endpoint", "testing my deletes", testDeleteResponse)
    val emptyTestGetInfo = MethodInfo("No request params and response body", "testing more")
  }

  private fun Application.configModule() {
    install(ContentNegotiation) {
      jackson {
        enable(SerializationFeature.INDENT_OUTPUT)
        setSerializationInclusion(JsonInclude.Include.NON_NULL)
      }
    }
  }

  private fun Application.notarizedGetModule() {
    routing {
      route("/test") {
        notarizedGet<TestParams, TestResponse>(testGetInfo) {
          call.respondText { "hey dude ‼️ congratz on the get request" }
        }
      }
    }
  }

  private fun Application.notarizedPostModule() {
    routing {
      route("/test") {
        notarizedPost<TestParams, TestRequest, TestCreatedResponse>(testPostInfo) {
          call.respondText { "hey dude ✌️ congratz on the post request" }
        }
      }
    }
  }

  private fun Application.notarizedDeleteModule() {
    routing {
      route("/test") {
        notarizedDelete<TestParams, Unit>(testDeleteInfo) {
          call.respond(HttpStatusCode.NoContent)
        }
      }
    }
  }

  private fun Application.notarizedPutModule() {
    routing {
      route("/test") {
        notarizedPut<TestParams, TestRequest, TestCreatedResponse>(testPutInfo) {
          call.respondText { "hey pal 🌝 whatcha doin' here?" }
        }
      }
    }
  }

  private fun Application.pathParsingTestModule() {
    routing {
      route("/this") {
        route("/is") {
          route("/a") {
            route("/complex") {
              route("path") {
                route("with/an/{id}") {
                  notarizedGet<TestParams, TestResponse>(testGetInfo) {
                    call.respondText { "Aww you followed this whole route 🥺" }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  private fun Application.rootModule() {
    routing {
      route("/") {
        notarizedGet<TestParams, TestResponse>(testGetInfo) {
          call.respondText { "☎️🏠🌲" }
        }
      }
    }
  }

  private fun Application.nestedUnderRootModule() {
    routing {
      route("/") {
        route("/testerino") {
          notarizedGet<TestParams, TestResponse>(testGetInfo) {
            call.respondText { "🤔🔥" }
          }
        }
      }
    }
  }

  private fun Application.trailingSlash() {
    routing {
      route("/test") {
        route("/") {
          notarizedGet<TestParams, TestResponse>(testGetInfo) {
            call.respondText { "🙀👾" }
          }
        }
      }
    }
  }

  private fun Application.returnsList() {
    routing {
      route("/test") {
        notarizedGet<TestParams, List<TestResponse>>(testGetInfo) {
          call.respondText { "hey dude ur doing amazing work!" }
        }
      }
    }
  }

  private fun Application.complexType() {
    routing {
      route("/test") {
        notarizedPut<Unit, ComplexRequest, TestResponse>(testPutInfo) {
          call.respondText { "heya" }
        }
      }
    }
  }

  private fun Application.primitives() {
    routing {
      route("/test") {
        notarizedPut<Unit, Int, Boolean>(testPutInfo) {
          call.respondText { "heya" }
        }
      }
    }
  }

  private fun Application.emptyGet() {
    routing {
      route("/test/empty") {
        notarizedGet<Unit, Unit>(emptyTestGetInfo) {
          call.respond(HttpStatusCode.OK)
        }
      }
    }
  }

  data class OptionalParams(@QueryParam val required: String, @QueryParam val notRequired: String?)
  private fun Application.nonRequiredParamsGet() {
    routing {
      route("/test/optional") {
        notarizedGet<OptionalParams, Unit>(emptyTestGetInfo) {
          call.respond(HttpStatusCode.OK)
        }
      }
    }
  }

  private val oas = Kompendium.openApiSpec.copy(
    info = OpenApiSpecInfo(
      title = "Test API",
      version = "1.33.7",
      description = "An amazing, fully-ish 😉 generated API spec",
      termsOfService = URI("https://example.com"),
      contact = OpenApiSpecInfoContact(
        name = "Homer Simpson",
        email = "chunkylover53@aol.com",
        url = URI("https://gph.is/1NPUDiM")
      ),
      license = OpenApiSpecInfoLicense(
        name = "MIT",
        url = URI("https://github.com/lg-backbone/kompendium/blob/main/LICENSE")
      )
    ),
    servers = mutableListOf(
      OpenApiSpecServer(
        url = URI("https://myawesomeapi.com"),
        description = "Production instance of my API"
      ),
      OpenApiSpecServer(
        url = URI("https://staging.myawesomeapi.com"),
        description = "Where the fun stuff happens"
      )
    )
  )

  private fun Application.docs() {
    routing {
      openApi(oas)
      redoc(oas)
    }
  }

}
