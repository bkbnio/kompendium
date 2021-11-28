package io.bkbn.kompendium.core

import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import java.net.URI
import io.bkbn.kompendium.core.routes.openApi
import io.bkbn.kompendium.core.routes.redoc
import io.bkbn.kompendium.core.util.TestHelpers.OPEN_API_ENDPOINT
import io.bkbn.kompendium.core.util.TestHelpers.getFileSnapshot
import io.bkbn.kompendium.core.util.complexType
import io.bkbn.kompendium.core.util.docs
import io.bkbn.kompendium.core.util.jacksonConfigModule
import io.bkbn.kompendium.core.util.emptyGet
import io.bkbn.kompendium.core.util.genericPolymorphicResponse
import io.bkbn.kompendium.core.util.genericPolymorphicResponseMultipleImpls
import io.bkbn.kompendium.core.util.headerParameter
import io.bkbn.kompendium.core.util.kotlinxConfigModule
import io.bkbn.kompendium.core.util.nestedUnderRootModule
import io.bkbn.kompendium.core.util.nonRequiredParamsGet
import io.bkbn.kompendium.core.util.notarizedDeleteModule
import io.bkbn.kompendium.core.util.notarizedGetModule
import io.bkbn.kompendium.core.util.notarizedGetWithMultipleThrowables
import io.bkbn.kompendium.core.util.notarizedGetWithNotarizedException
import io.bkbn.kompendium.core.util.notarizedPostModule
import io.bkbn.kompendium.core.util.notarizedPutModule
import io.bkbn.kompendium.core.util.pathParsingTestModule
import io.bkbn.kompendium.core.util.polymorphicCollectionResponse
import io.bkbn.kompendium.core.util.polymorphicInterfaceResponse
import io.bkbn.kompendium.core.util.polymorphicMapResponse
import io.bkbn.kompendium.core.util.polymorphicResponse
import io.bkbn.kompendium.core.util.primitives
import io.bkbn.kompendium.core.util.returnsList
import io.bkbn.kompendium.core.util.rootModule
import io.bkbn.kompendium.core.util.simpleGenericResponse
import io.bkbn.kompendium.core.util.statusPageModule
import io.bkbn.kompendium.core.util.statusPageMultiExceptions
import io.bkbn.kompendium.core.util.trailingSlash
import io.bkbn.kompendium.core.util.undeclaredType
import io.bkbn.kompendium.core.util.withDefaultParameter
import io.bkbn.kompendium.core.util.withExamples
import io.bkbn.kompendium.core.util.withOperationId
import io.bkbn.kompendium.oas.info.Contact
import io.bkbn.kompendium.oas.info.Info
import io.bkbn.kompendium.oas.info.License
import io.bkbn.kompendium.oas.server.Server
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class KompendiumTest : DescribeSpec({
  afterEach { Kompendium.resetSchema() }
  describe("Notarized Open API Metadata Tests") {
    it("Can notarize a get request") {
      withTestApplication({
        kotlinxConfigModule()
        docs()
        notarizedGetModule()
      }) {
        // arrange
        val expected = getFileSnapshot("notarized_get.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content!! shouldEqualJson expected
        }
      }
    }
    it("Can notarize a post request") {
      withTestApplication({
        jacksonConfigModule()
        docs()
        notarizedPostModule()
      }) {
        // arrange
        val expected = getFileSnapshot("notarized_post.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content!! shouldEqualJson expected
        }
      }
    }
    it("Can notarize a put request") {
      withTestApplication({
        jacksonConfigModule()
        docs()
        notarizedPutModule()
      }) {
        // arrange
        val expected = getFileSnapshot("notarized_put.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content!! shouldEqualJson expected
        }
      }
    }
    it("Can notarize a delete request") {
      withTestApplication({
        jacksonConfigModule()
        docs()
        notarizedDeleteModule()
      }) {
        // arrange
        val expected = getFileSnapshot("notarized_delete.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content!! shouldEqualJson expected
        }
      }
    }
    it("Can notarize a complex type") {
      withTestApplication({
        jacksonConfigModule()
        docs()
        complexType()
      }) {
        // arrange
        val expected = getFileSnapshot("complex_type.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content!! shouldEqualJson expected
        }
      }
    }
    it("Can notarize primitives") {
      withTestApplication({
        jacksonConfigModule()
        docs()
        primitives()
      }) {
        // arrange
        val expected = getFileSnapshot("notarized_primitives.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content!! shouldEqualJson expected
        }
      }
    }
    it("Can notarize a top level list response") {
      withTestApplication({
        jacksonConfigModule()
        docs()
        returnsList()
      }) {
        // arrange
        val expected = getFileSnapshot("response_list.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content!! shouldEqualJson expected
        }
      }
    }
    it("Can notarize a route with no request params and no response body") {
      withTestApplication({
        kotlinxConfigModule()
        docs()
        emptyGet()
      }) {
        // arrange
        val expected = getFileSnapshot("no_request_params_and_no_response_body.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content!! shouldEqualJson expected
        }
      }
    }
    it("Can notarize a route with non-required params") {
      withTestApplication({
        jacksonConfigModule()
        docs()
        nonRequiredParamsGet()
      }) {
        // arrange
        val expected = getFileSnapshot("non_required_params.json").trim()

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
  describe("Notarized Ktor Functionality Tests") {
    it("Can notarized a get request and return the expected result") {
      withTestApplication({
        jacksonConfigModule()
        docs()
        notarizedGetModule()
      }) {
        // arrange
        val expected = "hey dude ‼️ congratz on the get request"

        // act
        handleRequest(HttpMethod.Get, "/test").apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content shouldBe expected
        }
      }
    }
    it("Can notarize a post request and return the expected result") {
      withTestApplication({
        kotlinxConfigModule()
        docs()
        notarizedPostModule()
      }) {
        // arrange
        val expected = "hey dude ✌️ congratz on the post request"

        // act
        handleRequest(HttpMethod.Post, "/test").apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content shouldBe expected
        }
      }
    }
    it("Can notarize a put request and return the expected result") {
      withTestApplication({
        kotlinxConfigModule()
        docs()
        notarizedPutModule()
      }) {
        // arrange
        val expected = "hey pal 🌝 whatcha doin' here?"

        // act
        handleRequest(HttpMethod.Put, "/test").apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content shouldBe expected
        }
      }
    }
    it("Can notarize a put request and return the expected result") {
      withTestApplication({
        kotlinxConfigModule()
        docs()
        notarizedDeleteModule()
      }) {
        // act
        handleRequest(HttpMethod.Delete, "/test").apply {
          // assert
          response shouldHaveStatus HttpStatusCode.NoContent
          response.content shouldBe null
        }
      }
    }
    it("Can notarize the root route and return the expected result") {
      withTestApplication({
        kotlinxConfigModule()
        docs()
        rootModule()
      }) {
        // arrange
        val expected = "☎️🏠🌲"

        // act
        handleRequest(HttpMethod.Get, "/").apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content shouldBe expected
        }
      }
    }
    it("Can notarize a trailing slash route and return the expected result") {
      withTestApplication({
        jacksonConfigModule()
        docs()
        trailingSlash()
      }) {
        // arrange
        val expected = "🙀👾"

        // act
        handleRequest(HttpMethod.Get, "/test/").apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content shouldBe expected
        }
      }
    }
  }
  describe("Route Parsing") {
    it("Can parse a simple path and store it under the expected route") {
      withTestApplication({
        jacksonConfigModule()
        docs()
        pathParsingTestModule()
      }) {
        // arrange
        val expected = getFileSnapshot("path_parser.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content shouldBe expected
        }
      }
    }
    it("Can notarize the root route") {
      withTestApplication({
        jacksonConfigModule()
        docs()
        rootModule()
      }) {
        // arrange
        val expected = getFileSnapshot("root_route.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content shouldBe expected
        }
      }
    }
    it("Can notarize a route under the root module without appending trailing slash") {
      withTestApplication({
        jacksonConfigModule()
        docs()
        nestedUnderRootModule()
      }) {
        // arrange
        val expected = getFileSnapshot("nested_under_root.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content shouldBe expected
        }
      }
    }
    it("Can notarize a route with a trailing slash") {
      withTestApplication({
        jacksonConfigModule()
        docs()
        trailingSlash()
      }) {
        // arrange
        val expected = getFileSnapshot("trailing_slash.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content shouldBe expected
        }
      }
    }
  }
  describe("Exceptions") {
    it("Can notarize a throwable") {
      withTestApplication({
        statusPageModule()
        jacksonConfigModule()
        docs()
        notarizedGetWithNotarizedException()
      }) {
        // arrange
        val expected = getFileSnapshot("notarized_get_with_exception_response.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content shouldBe expected
        }
      }
    }
    it("Can notarize multiple throwables") {
      withTestApplication({
        statusPageMultiExceptions()
        jacksonConfigModule()
        docs()
        notarizedGetWithMultipleThrowables()
      }) {
        // arrange
        val expected = getFileSnapshot("notarized_get_with_multiple_exception_responses.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content shouldBe expected
        }
      }
    }
  }
  describe("Examples") {
    it("Can generate example response and request bodies") {
      withTestApplication({
        kotlinxConfigModule()
        docs()
        withExamples()
      }) {
        // arrange
        val expected = getFileSnapshot("example_req_and_resp.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content shouldBe expected
        }
      }
    }
  }
  describe("Defaults") {
    it("Can generate a default parameter values") {
      withTestApplication({
        jacksonConfigModule()
        docs()
        withDefaultParameter()
      }) {
        // arrange
        val expected = getFileSnapshot("query_with_default_parameter.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content shouldBe expected
        }
      }
    }
  }
  describe("Polymorphism and Generics") {
    it("can generate a polymorphic response type") {
      withTestApplication({
        jacksonConfigModule()
        docs()
        polymorphicResponse()
      }) {
        // arrange
        val expected = getFileSnapshot("polymorphic_response.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content shouldBe expected
        }
      }
    }
    it("Can generate a collection with polymorphic response type") {
      withTestApplication({
        jacksonConfigModule()
        docs()
        polymorphicCollectionResponse()
      }) {
        // arrange
        val expected = getFileSnapshot("polymorphic_list_response.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content shouldBe expected
        }
      }
    }
    it("Can generate a map with a polymorphic response type") {
      withTestApplication({
        jacksonConfigModule()
        docs()
        polymorphicMapResponse()
      }) {
        // arrange
        val expected = getFileSnapshot("polymorphic_map_response.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content shouldBe expected
        }
      }
    }
    it("Can generate a polymorphic response from a sealed interface") {
      withTestApplication({
        jacksonConfigModule()
        docs()
        polymorphicInterfaceResponse()
      }) {
        // arrange
        val expected = getFileSnapshot("sealed_interface_response.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content shouldBe expected
        }
      }
    }
    it("Can generate a response type with a generic type") {
      withTestApplication({
        jacksonConfigModule()
        docs()
        simpleGenericResponse()
      }) {
        // arrange
        val expected = getFileSnapshot("generic_response.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content shouldBe expected
        }
      }
    }
    it("Can generate a polymorphic response type with generics") {
      withTestApplication({
        jacksonConfigModule()
        docs()
        genericPolymorphicResponse()
      }) {
        // arrange
        val expected = getFileSnapshot("polymorphic_response_with_generics.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content shouldBe expected
        }
      }
    }
    it("Can handle an absolutely psycho inheritance test") {
      withTestApplication({
        kotlinxConfigModule()
        docs()
        genericPolymorphicResponseMultipleImpls()
      }) {
        // arrange
        val expected = getFileSnapshot("crazy_polymorphic_example.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content shouldBe expected
        }
      }
    }
  }
  describe("Miscellaneous") {
    it("Can generate the necessary ReDoc home page") {
      withTestApplication({
        jacksonConfigModule()
        docs()
        returnsList()
      }) {
        // arrange
        val expected = getFileSnapshot("redoc.html")

        // act
        handleRequest(HttpMethod.Get, "/docs").apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content shouldBe expected
        }
      }
    }
    it("Can add an operation id to a notarized route") {
      withTestApplication({
        jacksonConfigModule()
        docs()
        withOperationId()
      }) {
        // arrange
        val expected = getFileSnapshot("notarized_get_with_operation_id.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content!! shouldEqualJson expected
        }
      }
    }
    it("Can add an undeclared field") {
      withTestApplication({
        kotlinxConfigModule()
        docs()
        undeclaredType()
      }) {
        // arrange
        val expected = getFileSnapshot("undeclared_field.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content shouldBe expected
        }
      }
    }
    it("Can add a custom header parameter with a name override") {
      withTestApplication({
        jacksonConfigModule()
        docs()
        headerParameter()
      }) {
        // arrange
        val expected = getFileSnapshot("override_parameter_name.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content shouldBe expected
        }
      }
    }
  }
})
