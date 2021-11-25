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
import io.bkbn.kompendium.util.jacksonConfigModule
import io.bkbn.kompendium.util.emptyGet
import io.bkbn.kompendium.util.genericPolymorphicResponse
import io.bkbn.kompendium.util.genericPolymorphicResponseMultipleImpls
import io.bkbn.kompendium.util.kotlinxConfigModule
import io.bkbn.kompendium.util.nestedUnderRootModule
import io.bkbn.kompendium.util.nonRequiredParamsGet
import io.bkbn.kompendium.util.notarizedDeleteModule
import io.bkbn.kompendium.util.notarizedGetModule
import io.bkbn.kompendium.util.notarizedGetWithMultipleThrowables
import io.bkbn.kompendium.util.notarizedGetWithNotarizedException
import io.bkbn.kompendium.util.notarizedPostModule
import io.bkbn.kompendium.util.notarizedPutModule
import io.bkbn.kompendium.util.pathParsingTestModule
import io.bkbn.kompendium.util.polymorphicCollectionResponse
import io.bkbn.kompendium.util.polymorphicInterfaceResponse
import io.bkbn.kompendium.util.polymorphicMapResponse
import io.bkbn.kompendium.util.polymorphicResponse
import io.bkbn.kompendium.util.primitives
import io.bkbn.kompendium.util.returnsList
import io.bkbn.kompendium.util.rootModule
import io.bkbn.kompendium.util.simpleGenericResponse
import io.bkbn.kompendium.util.statusPageModule
import io.bkbn.kompendium.util.statusPageMultiExceptions
import io.bkbn.kompendium.util.trailingSlash
import io.bkbn.kompendium.util.undeclaredType
import io.bkbn.kompendium.util.withDefaultParameter
import io.bkbn.kompendium.util.withExamples
import io.bkbn.kompendium.util.withOperationId

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
      kotlinxConfigModule()
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
      jacksonConfigModule()
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
      jacksonConfigModule()
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
      kotlinxConfigModule()
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
      jacksonConfigModule()
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
      jacksonConfigModule()
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
      jacksonConfigModule()
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
      jacksonConfigModule()
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
      jacksonConfigModule()
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
      jacksonConfigModule()
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
      kotlinxConfigModule()
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
      jacksonConfigModule()
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
      jacksonConfigModule()
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
      jacksonConfigModule()
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
      jacksonConfigModule()
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
      jacksonConfigModule()
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
      jacksonConfigModule()
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
      kotlinxConfigModule()
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
      jacksonConfigModule()
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
  fun `Can add operationId`() {
    withTestApplication({
      jacksonConfigModule()
      docs()
      withOperationId()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = getFileSnapshot("notarized_get_with_operation_id.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Generates the expected redoc`() {
    withTestApplication({
      jacksonConfigModule()
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
      jacksonConfigModule()
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
      jacksonConfigModule()
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
      kotlinxConfigModule()
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
      jacksonConfigModule()
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

  @Test
  fun `Can generate a polymorphic response type`() {
    withTestApplication({
      jacksonConfigModule()
      docs()
      polymorphicResponse()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = getFileSnapshot("polymorphic_response.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Can generate a collection with polymorphic response type`() {
    withTestApplication({
      jacksonConfigModule()
      docs()
      polymorphicCollectionResponse()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = getFileSnapshot("polymorphic_list_response.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Can generate a map with a polymorphic response type`() {
    withTestApplication({
      jacksonConfigModule()
      docs()
      polymorphicMapResponse()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = getFileSnapshot("polymorphic_map_response.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Can generate a polymorphic response from a sealed interface`() {
    withTestApplication({
      jacksonConfigModule()
      docs()
      polymorphicInterfaceResponse()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = getFileSnapshot("sealed_interface_response.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Can generate a response type with a generic type`() {
    withTestApplication({
      jacksonConfigModule()
      docs()
      simpleGenericResponse()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = getFileSnapshot("generic_response.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Can generate a polymorphic response type with generics`() {
    withTestApplication({
      jacksonConfigModule()
      docs()
      genericPolymorphicResponse()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = getFileSnapshot("polymorphic_response_with_generics.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Absolute Psycho Inheritance Test`() {
    withTestApplication({
      kotlinxConfigModule()
      docs()
      genericPolymorphicResponseMultipleImpls()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = getFileSnapshot("crazy_polymorphic_example.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Can add an undeclared field`() {
    withTestApplication({
      kotlinxConfigModule()
      docs()
      undeclaredType()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = getFileSnapshot("undeclared_field.json").trim()
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
