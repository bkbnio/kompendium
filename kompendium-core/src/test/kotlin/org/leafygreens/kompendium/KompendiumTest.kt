package org.leafygreens.kompendium

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
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
import org.leafygreens.kompendium.Notarized.notarizedDelete
import org.leafygreens.kompendium.Notarized.notarizedException
import org.leafygreens.kompendium.Notarized.notarizedGet
import org.leafygreens.kompendium.Notarized.notarizedPost
import org.leafygreens.kompendium.Notarized.notarizedPut
import org.leafygreens.kompendium.annotations.KompendiumParam
import org.leafygreens.kompendium.annotations.ParamType
import org.leafygreens.kompendium.models.meta.MethodInfo.DeleteInfo
import org.leafygreens.kompendium.models.meta.MethodInfo.GetInfo
import org.leafygreens.kompendium.models.meta.MethodInfo.PostInfo
import org.leafygreens.kompendium.models.meta.MethodInfo.PutInfo
import org.leafygreens.kompendium.models.meta.RequestInfo
import org.leafygreens.kompendium.models.meta.ResponseInfo
import org.leafygreens.kompendium.models.oas.OpenApiSpecInfo
import org.leafygreens.kompendium.models.oas.OpenApiSpecInfoContact
import org.leafygreens.kompendium.models.oas.OpenApiSpecInfoLicense
import org.leafygreens.kompendium.models.oas.OpenApiSpecServer
import org.leafygreens.kompendium.routes.openApi
import org.leafygreens.kompendium.routes.redoc
import org.leafygreens.kompendium.util.ComplexRequest
import org.leafygreens.kompendium.util.DefaultParameter
import org.leafygreens.kompendium.util.ExceptionResponse
import org.leafygreens.kompendium.util.KompendiumHttpCodes
import org.leafygreens.kompendium.util.OptionalParams
import org.leafygreens.kompendium.util.TestCreatedResponse
import org.leafygreens.kompendium.util.TestHelpers.getFileSnapshot
import org.leafygreens.kompendium.util.TestNested
import org.leafygreens.kompendium.util.TestParams
import org.leafygreens.kompendium.util.TestRequest
import org.leafygreens.kompendium.util.TestResponse
import org.leafygreens.kompendium.util.TestResponseInfo.emptyTestGetInfo
import org.leafygreens.kompendium.util.TestResponseInfo.testDeleteInfo
import org.leafygreens.kompendium.util.TestResponseInfo.testGetInfo
import org.leafygreens.kompendium.util.TestResponseInfo.testGetInfoAgain
import org.leafygreens.kompendium.util.TestResponseInfo.testGetWithException
import org.leafygreens.kompendium.util.TestResponseInfo.testGetWithMultipleExceptions
import org.leafygreens.kompendium.util.TestResponseInfo.testPostInfo
import org.leafygreens.kompendium.util.TestResponseInfo.testPutInfo
import org.leafygreens.kompendium.util.TestResponseInfo.testPutInfoAgain
import org.leafygreens.kompendium.util.TestResponseInfo.testPutInfoAlso
import org.leafygreens.kompendium.util.TestResponseInfo.trulyEmptyTestGetInfo

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

  private fun Application.configModule() {
    install(ContentNegotiation) {
      jackson {
        enable(SerializationFeature.INDENT_OUTPUT)
        setSerializationInclusion(JsonInclude.Include.NON_NULL)
      }
    }
  }

  private fun Application.statusPageModule() {
    install(StatusPages) {
      notarizedException<Exception, ExceptionResponse>(info = ResponseInfo(400, "Bad Things Happened")) {
        call.respond(HttpStatusCode.BadRequest, ExceptionResponse("Why you do dis?"))
      }
    }
  }

  private fun Application.statusPageMultiExceptions() {
    install(StatusPages) {
      notarizedException<AccessDeniedException, Unit>(info = ResponseInfo(403, "New API who dis?")) {
        call.respond(HttpStatusCode.Forbidden)
      }
      notarizedException<Exception, ExceptionResponse>(info = ResponseInfo(400, "Bad Things Happened")) {
        call.respond(HttpStatusCode.BadRequest, ExceptionResponse("Why you do dis?"))
      }
    }
  }

  private fun Application.notarizedGetWithNotarizedException() {
    routing {
      route("/test") {
        notarizedGet(testGetWithException) {
          error("something terrible has happened!")
        }
      }
    }
  }

  private fun Application.notarizedGetWithMultipleThrowables() {
    routing {
      route("/test") {
        notarizedGet(testGetWithMultipleExceptions) {
          error("something terrible has happened!")
        }
      }
    }
  }

  private fun Application.notarizedGetModule() {
    routing {
      route("/test") {
        notarizedGet(testGetInfo) {
          call.respondText { "hey dude ‼️ congratz on the get request" }
        }
      }
    }
  }

  private fun Application.notarizedPostModule() {
    routing {
      route("/test") {
        notarizedPost(testPostInfo) {
          call.respondText { "hey dude ✌️ congratz on the post request" }
        }
      }
    }
  }

  private fun Application.notarizedDeleteModule() {
    routing {
      route("/test") {
        notarizedDelete(testDeleteInfo) {
          call.respond(HttpStatusCode.NoContent)
        }
      }
    }
  }

  private fun Application.notarizedPutModule() {
    routing {
      route("/test") {
        notarizedPut(testPutInfoAlso) {
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
                  notarizedGet(testGetInfo) {
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
        notarizedGet(testGetInfo) {
          call.respondText { "☎️🏠🌲" }
        }
      }
    }
  }

  private fun Application.nestedUnderRootModule() {
    routing {
      route("/") {
        route("/testerino") {
          notarizedGet(testGetInfo) {
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
          notarizedGet(testGetInfo) {
            call.respondText { "🙀👾" }
          }
        }
      }
    }
  }

  private fun Application.returnsList() {
    routing {
      route("/test") {
        notarizedGet(testGetInfoAgain) {
          call.respondText { "hey dude ur doing amazing work!" }
        }
      }
    }
  }

  private fun Application.complexType() {
    routing {
      route("/test") {
        notarizedPut(testPutInfo) {
          call.respondText { "heya" }
        }
      }
    }
  }

  private fun Application.primitives() {
    routing {
      route("/test") {
        notarizedPut(testPutInfoAgain) {
          call.respondText { "heya" }
        }
      }
    }
  }

  private fun Application.emptyGet() {
    routing {
      route("/test/empty") {
        notarizedGet(trulyEmptyTestGetInfo) {
          call.respond(HttpStatusCode.OK)
        }
      }
    }
  }

  private fun Application.withExamples() {
    routing {
      route("/test/examples") {
        notarizedPost(
          info = PostInfo<Unit, TestRequest, TestResponse>(
            summary = "Example Parameters",
            description = "A test for setting parameter examples",
            requestInfo = RequestInfo(
              description = "Test",
              examples = mapOf(
                "one" to TestRequest(fieldName = TestNested(nesty = "hey"), b = 4.0, aaa = emptyList()),
                "two" to TestRequest(fieldName = TestNested(nesty = "hello"), b = 3.8, aaa = listOf(31324234))
              )
            ),
            responseInfo = ResponseInfo(
              status = 201,
              description = "nice",
              examples = mapOf("test" to TestResponse(c = "spud"))
            ),
          )
        ) {
          call.respond(HttpStatusCode.OK)
        }
      }
    }
  }

  private fun Application.withDefaultParameter() {
    routing {
      route("/test") {
        notarizedGet(
          info = GetInfo<DefaultParameter, TestResponse>(
            summary = "Testing Default Params",
            description = "Should have a default parameter value"
          )
        ) {
          call.respond(TestResponse("hey"))
        }
      }
    }
  }

  private fun Application.nonRequiredParamsGet() {
    routing {
      route("/test/optional") {
        notarizedGet(emptyTestGetInfo) {
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
