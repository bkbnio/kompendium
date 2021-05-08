package io.bkbn.kompendium

import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import java.net.URI
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import io.bkbn.kompendium.models.oas.OpenApiSpecInfo
import io.bkbn.kompendium.models.oas.OpenApiSpecInfoContact
import io.bkbn.kompendium.models.oas.OpenApiSpecInfoLicense
import io.bkbn.kompendium.models.oas.OpenApiSpecServer
import io.bkbn.kompendium.routes.openApi
import io.bkbn.kompendium.routes.redoc
import io.bkbn.kompendium.util.TestHelpers.getFileSnapshot
import io.bkbn.kompendium.util.complexType
import io.bkbn.kompendium.util.configModule
import io.bkbn.kompendium.util.emptyGet
import io.bkbn.kompendium.util.nestedUnderRootModule
import io.bkbn.kompendium.util.nonRequiredParamsGet
import io.bkbn.kompendium.util.notarizedDeleteModule
import io.bkbn.kompendium.util.notarizedGetModule
import io.bkbn.kompendium.util.notarizedGetWithMultipleThrowables
import io.bkbn.kompendium.util.notarizedGetWithNotarizedException
import io.bkbn.kompendium.util.notarizedPostModule
import io.bkbn.kompendium.util.notarizedPutModule
import io.bkbn.kompendium.util.pathParsingTestModule
import io.bkbn.kompendium.util.primitives
import io.bkbn.kompendium.util.returnsList
import io.bkbn.kompendium.util.rootModule
import io.bkbn.kompendium.util.statusPageModule
import io.bkbn.kompendium.util.statusPageMultiExceptions
import io.bkbn.kompendium.util.trailingSlash
import io.bkbn.kompendium.util.withDefaultParameter
import io.bkbn.kompendium.util.withExamples

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
      val expected = getFileSnapshot("notarized_get.json").trim()
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
      val expected = getFileSnapshot("notarized_post.json").trim()
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
      val expected = getFileSnapshot("notarized_put.json").trim()
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
      val expected = getFileSnapshot("notarized_delete.json").trim()
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
      val expected = getFileSnapshot("path_parser.json").trim()
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
      val expected = getFileSnapshot("root_route.json").trim()
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
      val expected = getFileSnapshot("nested_under_root.json").trim()
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
      val expected = getFileSnapshot("trailing_slash.json").trim()
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
      val expected = getFileSnapshot("complex_type.json").trim()
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
      val expected = getFileSnapshot("notarized_primitives.json").trim()
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
      val expected = getFileSnapshot("response_list.json").trim()
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
      val expected = getFileSnapshot("no_request_params_and_no_response_body.json").trim()
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
      val expected = getFileSnapshot("non_required_params.json").trim()
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
      val expected = getFileSnapshot("redoc.html")
      assertEquals(expected, html)
    }
  }

  @Test
  fun `Generates additional responses when passed a throwable`() {
    withTestApplication({
      statusPageModule()
      configModule()
      docs()
      notarizedGetWithNotarizedException()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = getFileSnapshot("notarized_get_with_exception_response.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Generates additional responses when passed multiple throwables`() {
    withTestApplication({
      statusPageMultiExceptions()
      configModule()
      docs()
      notarizedGetWithMultipleThrowables()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = getFileSnapshot("notarized_get_with_multiple_exception_responses.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Can generate example response and request bodies`() {
    withTestApplication({
      configModule()
      docs()
      withExamples()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = getFileSnapshot("example_req_and_resp.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Can generate a default parameter value`() {
    withTestApplication({
      configModule()
      docs()
      withDefaultParameter()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = getFileSnapshot("query_with_default_parameter.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
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
        url = URI("https://github.com/bkbnio/kompendium/blob/main/LICENSE")
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
