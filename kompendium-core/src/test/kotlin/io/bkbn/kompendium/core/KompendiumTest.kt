package io.bkbn.kompendium.core

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.bkbn.kompendium.core.TestHelpers.OPEN_API_ENDPOINT
import io.bkbn.kompendium.core.TestHelpers.compareOpenAPISpec
import io.bkbn.kompendium.core.TestHelpers.getFileSnapshot
import io.bkbn.kompendium.core.util.complexType
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
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class KompendiumTest : DescribeSpec({
  afterEach { Kompendium.resetSchema() }
  describe("Notarized Open API Metadata Tests") {
    it("Can notarize a get request") {
      // arrange
      withTestApplication({
        kotlinxConfigModule()
        docs()
        notarizedGetModule()
      }) {
        // act
        compareOpenAPISpec("notarized_get.json")
      }
    }
    it("Can notarize a post request") {
      // arrange
      withTestApplication({
        jacksonConfigModule()
        docs()
        notarizedPostModule()
      }) {
        // act
        compareOpenAPISpec("notarized_post.json")
      }
    }
    it("Can notarize a put request") {
      // arrange
      withTestApplication({
        jacksonConfigModule()
        docs()
        notarizedPutModule()
      }) {
        // act
        compareOpenAPISpec("notarized_put.json")
      }
    }
    it("Can notarize a delete request") {
      // arrange
      withTestApplication({
        jacksonConfigModule()
        docs()
        notarizedDeleteModule()
      }) {
        // act
        compareOpenAPISpec("notarized_delete.json")
      }
    }
    it("Can notarize a complex type") {
      // arrange
      withTestApplication({
        jacksonConfigModule()
        docs()
        complexType()
      }) {
        // act
        compareOpenAPISpec("complex_type.json")
      }
    }
    it("Can notarize primitives") {
      // arrange
      withTestApplication({
        jacksonConfigModule()
        docs()
        primitives()
      }) {
        // act
        compareOpenAPISpec("notarized_primitives.json")
      }
    }
    it("Can notarize a top level list response") {
      // arrange
      withTestApplication({
        jacksonConfigModule()
        docs()
        returnsList()
      }) {
        // act
        compareOpenAPISpec("response_list.json")
      }
    }
    it("Can notarize a route with no request params and no response body") {
      // arrange
      withTestApplication({
        kotlinxConfigModule()
        docs()
        emptyGet()
      }) {
        // act
        compareOpenAPISpec("no_request_params_and_no_response_body.json")
      }
    }
    it("Can notarize a route with non-required params") {
      // arrange
      withTestApplication({
        jacksonConfigModule()
        docs()
        nonRequiredParamsGet()
      }) {
        // act
        compareOpenAPISpec("non_required_params.json")
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
      // arrange
      withTestApplication({
        jacksonConfigModule()
        docs()
        pathParsingTestModule()
      }) {
        // act
        compareOpenAPISpec("path_parser.json")
      }
    }
    it("Can notarize the root route") {
      // arrange
      withTestApplication({
        jacksonConfigModule()
        docs()
        rootModule()
      }) {
        // act
        compareOpenAPISpec("root_route.json")
      }
    }
    it("Can notarize a route under the root module without appending trailing slash") {
      // arrange
      withTestApplication({
        jacksonConfigModule()
        docs()
        nestedUnderRootModule()
      }) {
        // act
        compareOpenAPISpec("nested_under_root.json")
      }
    }
    it("Can notarize a route with a trailing slash") {
      // arrange
      withTestApplication({
        jacksonConfigModule()
        docs()
        trailingSlash()
      }) {
        // act
        compareOpenAPISpec("trailing_slash.json")
      }
    }
  }
  describe("Exceptions") {
    it("Can notarize a throwable") {
      // arrange
      withTestApplication({
        statusPageModule()
        jacksonConfigModule()
        docs()
        notarizedGetWithNotarizedException()
      }) {
        // act
        compareOpenAPISpec("notarized_get_with_exception_response.json")
      }
    }
    it("Can notarize multiple throwables") {
      // arrange
      withTestApplication({
        statusPageMultiExceptions()
        jacksonConfigModule()
        docs()
        notarizedGetWithMultipleThrowables()
      }) {
        // act
        compareOpenAPISpec("notarized_get_with_multiple_exception_responses.json")
      }
    }
  }
  describe("Examples") {
    it("Can generate example response and request bodies") {
      // arrange
      withTestApplication({
        kotlinxConfigModule()
        docs()
        withExamples()
      }) {
        // act
        compareOpenAPISpec("example_req_and_resp.json")
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
        // act
        compareOpenAPISpec("query_with_default_parameter.json")
      }
    }
  }
  describe("Polymorphism and Generics") {
    it("can generate a polymorphic response type") {
      // arrange
      withTestApplication({
        jacksonConfigModule()
        docs()
        polymorphicResponse()
      }) {
        // act
        compareOpenAPISpec("polymorphic_response.json")
      }
    }
    it("Can generate a collection with polymorphic response type") {
      // arrange
      withTestApplication({
        jacksonConfigModule()
        docs()
        polymorphicCollectionResponse()
      }) {
        // act
        compareOpenAPISpec("polymorphic_list_response.json")
      }
    }
    it("Can generate a map with a polymorphic response type") {
      // arrange
      withTestApplication({
        jacksonConfigModule()
        docs()
        polymorphicMapResponse()
      }) {
        // act
        compareOpenAPISpec("polymorphic_map_response.json")
      }
    }
    it("Can generate a polymorphic response from a sealed interface") {
      // arrange
      withTestApplication({
        jacksonConfigModule()
        docs()
        polymorphicInterfaceResponse()
      }) {
        // act
        compareOpenAPISpec("sealed_interface_response.json")
      }
    }
    it("Can generate a response type with a generic type") {
      // arrange
      withTestApplication({
        jacksonConfigModule()
        docs()
        simpleGenericResponse()
      }) {
        // act
        compareOpenAPISpec("generic_response.json")
      }
    }
    it("Can generate a polymorphic response type with generics") {
      // arrange
      withTestApplication({
        jacksonConfigModule()
        docs()
        genericPolymorphicResponse()
      }) {
        // act
        compareOpenAPISpec("polymorphic_response_with_generics.json")
      }
    }
    it("Can handle an absolutely psycho inheritance test") {
      // arrange
      withTestApplication({
        kotlinxConfigModule()
        docs()
        genericPolymorphicResponseMultipleImpls()
      }) {
        // act
        compareOpenAPISpec("crazy_polymorphic_example.json")
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
      // arrange
      withTestApplication({
        jacksonConfigModule()
        docs()
        withOperationId()
      }) {
        // act
        compareOpenAPISpec("notarized_get_with_operation_id.json")
      }
    }
    it("Can add an undeclared field") {
      // arrange
      withTestApplication({
        kotlinxConfigModule()
        docs()
        undeclaredType()
      }) {
        // act
        compareOpenAPISpec("undeclared_field.json")
      }
    }
    it("Can add a custom header parameter with a name override") {
      // arrange
      withTestApplication({
        jacksonConfigModule()
        docs()
        headerParameter()
      }) {
        // act
        compareOpenAPISpec("override_parameter_name.json")
      }
    }
  }
})
