package io.bkbn.kompendium.core

import io.bkbn.kompendium.core.fixtures.TestHelpers.apiFunctionalityTest
import io.bkbn.kompendium.core.fixtures.TestHelpers.getFileSnapshot
import io.bkbn.kompendium.core.fixtures.TestHelpers.openApiTest
import io.bkbn.kompendium.core.util.complexType
import io.bkbn.kompendium.core.util.genericPolymorphicResponse
import io.bkbn.kompendium.core.util.genericPolymorphicResponseMultipleImpls
import io.bkbn.kompendium.core.util.headerParameter
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
import io.bkbn.kompendium.core.util.trailingSlash
import io.bkbn.kompendium.core.util.undeclaredType
import io.bkbn.kompendium.core.util.withDefaultParameter
import io.bkbn.kompendium.core.util.withExamples
import io.bkbn.kompendium.core.util.withOperationId
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode

class KompendiumTest : DescribeSpec({
  describe("Notarized Open API Metadata Tests") {
    it("Can notarize a get request") {
      // act
      openApiTest("notarized_get.json") { notarizedGetModule() }
    }
    it("Can notarize a post request") {
      // act
      openApiTest("notarized_post.json") { notarizedPostModule() }
    }
    it("Can notarize a put request") {
      // act
      openApiTest("notarized_put.json") { notarizedPutModule() }
    }
    it("Can notarize a delete request") {
      // act
      openApiTest("notarized_delete.json") { notarizedDeleteModule() }
    }
    it("Can notarize a complex type") {
      // act
      openApiTest("complex_type.json") { complexType() }
    }
    it("Can notarize primitives") {
      // act
      openApiTest("notarized_primitives.json") { primitives() }
    }
    it("Can notarize a top level list response") {
      // act
      openApiTest("response_list.json") { returnsList() }
    }
    it("Can notarize a route with non-required params") {
      // act
      openApiTest("non_required_params.json") { nonRequiredParamsGet() }
    }
  }
  describe("Notarized Ktor Functionality Tests") {
    it("Can notarized a get request and return the expected result") {
      // act
      apiFunctionalityTest("hey dude ‼️ congratz on the get request") { notarizedGetModule() }
    }
    it("Can notarize a post request and return the expected result") {
      // act
      apiFunctionalityTest(
        "hey dude ✌️ congratz on the post request",
        httpMethod = HttpMethod.Post
      ) { notarizedPostModule() }
    }
    it("Can notarize a put request and return the expected result") {
      // act
      apiFunctionalityTest("hey pal 🌝 whatcha doin' here?", httpMethod = HttpMethod.Put) { notarizedPutModule() }
    }
    it("Can notarize a delete request and return the expected result") {
      // act
      apiFunctionalityTest(
        null,
        httpMethod = HttpMethod.Delete,
        expectedStatusCode = HttpStatusCode.NoContent
      ) { notarizedDeleteModule() }
    }
    it("Can notarize the root route and return the expected result") {
      // act
      apiFunctionalityTest("☎️🏠🌲", "/") { rootModule() }
    }
    it("Can notarize a trailing slash route and return the expected result") {
      // act
      apiFunctionalityTest("🙀👾", "/test/") { trailingSlash() }
    }
  }
  describe("Route Parsing") {
    it("Can parse a simple path and store it under the expected route") {
      // act
      openApiTest("path_parser.json") { pathParsingTestModule() }
    }
    it("Can notarize the root route") {
      // act
      openApiTest("root_route.json") { rootModule() }
    }
    it("Can notarize a route under the root module without appending trailing slash") {
      // act
      openApiTest("nested_under_root.json") { nestedUnderRootModule() }
    }
    it("Can notarize a route with a trailing slash") {
      // act
      openApiTest("trailing_slash.json") { trailingSlash() }
    }
  }
  describe("Exceptions") {
    it("Can add an exception status code to a response") {
      // act
      openApiTest("notarized_get_with_exception_response.json") { notarizedGetWithNotarizedException() }
    }
    it("Can support multiple response codes") {
      // act
      openApiTest("notarized_get_with_multiple_exception_responses.json") { notarizedGetWithMultipleThrowables() }
    }
    it("Can add a polymorphic exception response") {
      TODO()
    }
    it("Can add a generic exception response") {
      TODO()
    }
  }
  describe("Examples") {
    it("Can generate example response and request bodies") {
      // act
      openApiTest("example_req_and_resp.json") { withExamples() }
    }
  }
  describe("Defaults") {
    it("Can generate a default parameter values") {
      // act
      openApiTest("query_with_default_parameter.json") { withDefaultParameter() }
    }
  }
  describe("Polymorphism and Generics") {
    it("can generate a polymorphic response type") {
      // act
      openApiTest("polymorphic_response.json") { polymorphicResponse() }
    }
    it("Can generate a collection with polymorphic response type") {
      // act
      openApiTest("polymorphic_list_response.json") { polymorphicCollectionResponse() }
    }
    it("Can generate a map with a polymorphic response type") {
      // act
      openApiTest("polymorphic_map_response.json") { polymorphicMapResponse() }
    }
    it("Can generate a polymorphic response from a sealed interface") {
      // act
      openApiTest("sealed_interface_response.json") { polymorphicInterfaceResponse() }
    }
    it("Can generate a response type with a generic type") {
      // act
      openApiTest("generic_response.json") { simpleGenericResponse() }
    }
    it("Can generate a polymorphic response type with generics") {
      // act
      openApiTest("polymorphic_response_with_generics.json") { genericPolymorphicResponse() }
    }
    it("Can handle an absolutely psycho inheritance test") {
      // act
      openApiTest("crazy_polymorphic_example.json") { genericPolymorphicResponseMultipleImpls() }
    }
  }
  describe("Miscellaneous") {
    it("Can generate the necessary ReDoc home page") {
      // act
      apiFunctionalityTest(getFileSnapshot("redoc.html"), "/docs") { returnsList() }
    }
    it("Can add an operation id to a notarized route") {
      // act
      openApiTest("notarized_get_with_operation_id.json") { withOperationId() }
    }
    it("Can add an undeclared field") {
      // act
      openApiTest("undeclared_field.json") { undeclaredType() }
    }
    it("Can add a custom header parameter with a name override") {
      // act
      openApiTest("override_parameter_name.json") { headerParameter() }
    }
  }
})
