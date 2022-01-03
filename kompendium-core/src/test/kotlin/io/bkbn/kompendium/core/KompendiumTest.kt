package io.bkbn.kompendium.core

import io.bkbn.kompendium.core.fixtures.TestHelpers.apiFunctionalityTest
import io.bkbn.kompendium.core.fixtures.TestHelpers.getFileSnapshot
import io.bkbn.kompendium.core.fixtures.TestHelpers.openApiTest
import io.bkbn.kompendium.core.util.complexType
import io.bkbn.kompendium.core.util.constrainedDoubleInfo
import io.bkbn.kompendium.core.util.constrainedIntInfo
import io.bkbn.kompendium.core.util.defaultField
import io.bkbn.kompendium.core.util.defaultParameter
import io.bkbn.kompendium.core.util.exclusiveMinMax
import io.bkbn.kompendium.core.util.formattedParam
import io.bkbn.kompendium.core.util.freeFormObject
import io.bkbn.kompendium.core.util.genericPolymorphicResponse
import io.bkbn.kompendium.core.util.genericPolymorphicResponseMultipleImpls
import io.bkbn.kompendium.core.util.headerParameter
import io.bkbn.kompendium.core.util.minMaxArray
import io.bkbn.kompendium.core.util.minMaxFreeForm
import io.bkbn.kompendium.core.util.minMaxString
import io.bkbn.kompendium.core.util.multipleOfDouble
import io.bkbn.kompendium.core.util.multipleOfInt
import io.bkbn.kompendium.core.util.nestedUnderRootModule
import io.bkbn.kompendium.core.util.nonRequiredParamsGet
import io.bkbn.kompendium.core.util.notarizedDeleteModule
import io.bkbn.kompendium.core.util.notarizedGetModule
import io.bkbn.kompendium.core.util.notarizedGetWithGenericErrorResponse
import io.bkbn.kompendium.core.util.notarizedGetWithMultipleThrowables
import io.bkbn.kompendium.core.util.notarizedGetWithNotarizedException
import io.bkbn.kompendium.core.util.notarizedGetWithPolymorphicErrorResponse
import io.bkbn.kompendium.core.util.notarizedHeadModule
import io.bkbn.kompendium.core.util.notarizedOptionsModule
import io.bkbn.kompendium.core.util.notarizedPatchModule
import io.bkbn.kompendium.core.util.notarizedPostModule
import io.bkbn.kompendium.core.util.notarizedPutModule
import io.bkbn.kompendium.core.util.nullableField
import io.bkbn.kompendium.core.util.overrideFieldInfo
import io.bkbn.kompendium.core.util.pathParsingTestModule
import io.bkbn.kompendium.core.util.polymorphicCollectionResponse
import io.bkbn.kompendium.core.util.polymorphicInterfaceResponse
import io.bkbn.kompendium.core.util.polymorphicMapResponse
import io.bkbn.kompendium.core.util.polymorphicResponse
import io.bkbn.kompendium.core.util.primitives
import io.bkbn.kompendium.core.util.regexString
import io.bkbn.kompendium.core.util.requiredParameter
import io.bkbn.kompendium.core.util.returnsList
import io.bkbn.kompendium.core.util.rootModule
import io.bkbn.kompendium.core.util.simpleGenericResponse
import io.bkbn.kompendium.core.util.trailingSlash
import io.bkbn.kompendium.core.util.undeclaredType
import io.bkbn.kompendium.core.util.uniqueArray
import io.bkbn.kompendium.core.util.withDefaultParameter
import io.bkbn.kompendium.core.util.withExamples
import io.bkbn.kompendium.core.util.withOperationId
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode

class KompendiumTest : DescribeSpec({
  describe("Notarized Open API Metadata Tests") {
    it("Can notarize a get request") {
      openApiTest("notarized_get.json") { notarizedGetModule() }
    }
    it("Can notarize a post request") {
      openApiTest("notarized_post.json") { notarizedPostModule() }
    }
    it("Can notarize a put request") {
      openApiTest("notarized_put.json") { notarizedPutModule() }
    }
    it("Can notarize a delete request") {
      openApiTest("notarized_delete.json") { notarizedDeleteModule() }
    }
    it("Can notarize a patch request") {
      openApiTest("notarized_patch.json") { notarizedPatchModule() }
    }
    it("Can notarize a head request") {
      openApiTest("notarized_head.json") { notarizedHeadModule() }
    }
    it("Can notarize an options request") {
      openApiTest("notarized_options.json") { notarizedOptionsModule() }
    }
    it("Can notarize a complex type") {
      openApiTest("complex_type.json") { complexType() }
    }
    it("Can notarize primitives") {
      openApiTest("notarized_primitives.json") { primitives() }
    }
    it("Can notarize a top level list response") {
      openApiTest("response_list.json") { returnsList() }
    }
    it("Can notarize a route with non-required params") {
      openApiTest("non_required_params.json") { nonRequiredParamsGet() }
    }
  }
  describe("Notarized Ktor Functionality Tests") {
    it("Can notarized a get request and return the expected result") {
      apiFunctionalityTest("hey dude ‼️ congratz on the get request") { notarizedGetModule() }
    }
    it("Can notarize a post request and return the expected result") {
      apiFunctionalityTest(
        "hey dude ✌️ congratz on the post request",
        httpMethod = HttpMethod.Post
      ) { notarizedPostModule() }
    }
    it("Can notarize a put request and return the expected result") {
      apiFunctionalityTest("hey pal 🌝 whatcha doin' here?", httpMethod = HttpMethod.Put) { notarizedPutModule() }
    }
    it("Can notarize a delete request and return the expected result") {
      apiFunctionalityTest(
        null,
        httpMethod = HttpMethod.Delete,
        expectedStatusCode = HttpStatusCode.NoContent
      ) { notarizedDeleteModule() }
    }
    it("Can notarize the root route and return the expected result") {
      apiFunctionalityTest("☎️🏠🌲", "/") { rootModule() }
    }
    it("Can notarize a trailing slash route and return the expected result") {
      apiFunctionalityTest("🙀👾", "/test/") { trailingSlash() }
    }
  }
  describe("Route Parsing") {
    it("Can parse a simple path and store it under the expected route") {
      openApiTest("path_parser.json") { pathParsingTestModule() }
    }
    it("Can notarize the root route") {
      openApiTest("root_route.json") { rootModule() }
    }
    it("Can notarize a route under the root module without appending trailing slash") {
      openApiTest("nested_under_root.json") { nestedUnderRootModule() }
    }
    it("Can notarize a route with a trailing slash") {
      openApiTest("trailing_slash.json") { trailingSlash() }
    }
  }
  describe("Exceptions") {
    it("Can add an exception status code to a response") {
      openApiTest("notarized_get_with_exception_response.json") { notarizedGetWithNotarizedException() }
    }
    it("Can support multiple response codes") {
      openApiTest("notarized_get_with_multiple_exception_responses.json") { notarizedGetWithMultipleThrowables() }
    }
    it("Can add a polymorphic exception response") {
      openApiTest("polymorphic_error_status_codes.json") { notarizedGetWithPolymorphicErrorResponse() }
    }
    it("Can add a generic exception response") {
      openApiTest("generic_exception.json") { notarizedGetWithGenericErrorResponse() }
    }
  }
  describe("Examples") {
    it("Can generate example response and request bodies") {
      openApiTest("example_req_and_resp.json") { withExamples() }
    }
  }
  describe("Defaults") {
    it("Can generate a default parameter values") {
      openApiTest("query_with_default_parameter.json") { withDefaultParameter() }
    }
  }
  describe("Required Fields") {
    it("Marks a parameter required if there is no default and it is not marked nullable") {
      openApiTest("required_param.json") { requiredParameter() }
    }
    it("Does not mark a parameter as required if a default value is provided") {
      openApiTest("default_param.json") { defaultParameter() }
    }
    it("Does not mark a field as required if a default value is provided") {
      openApiTest("default_field.json") { defaultField() }
    }
    it("Marks a field as nullable when expected") {
      openApiTest("nullable_field.json") { nullableField() }
    }
  }
  describe("Polymorphism and Generics") {
    it("can generate a polymorphic response type") {
      openApiTest("polymorphic_response.json") { polymorphicResponse() }
    }
    it("Can generate a collection with polymorphic response type") {
      openApiTest("polymorphic_list_response.json") { polymorphicCollectionResponse() }
    }
    it("Can generate a map with a polymorphic response type") {
      openApiTest("polymorphic_map_response.json") { polymorphicMapResponse() }
    }
    it("Can generate a polymorphic response from a sealed interface") {
      openApiTest("sealed_interface_response.json") { polymorphicInterfaceResponse() }
    }
    it("Can generate a response type with a generic type") {
      openApiTest("generic_response.json") { simpleGenericResponse() }
    }
    it("Can generate a polymorphic response type with generics") {
      openApiTest("polymorphic_response_with_generics.json") { genericPolymorphicResponse() }
    }
    it("Can handle an absolutely psycho inheritance test") {
      openApiTest("crazy_polymorphic_example.json") { genericPolymorphicResponseMultipleImpls() }
    }
  }
  describe("Miscellaneous") {
    it("Can generate the necessary ReDoc home page") {
      apiFunctionalityTest(getFileSnapshot("redoc.html"), "/docs") { returnsList() }
    }
    it("Can add an operation id to a notarized route") {
      openApiTest("notarized_get_with_operation_id.json") { withOperationId() }
    }
    it("Can add an undeclared field") {
      openApiTest("undeclared_field.json") { undeclaredType() }
    }
    it("Can add a custom header parameter with a name override") {
      openApiTest("override_parameter_name.json") { headerParameter() }
    }
    it("Can override field values via annotation") {
      openApiTest("field_override.json") { overrideFieldInfo() }
    }
  }
  describe("Constraints") {
    it("Can set a minimum and maximum integer value") {
      openApiTest("min_max_int_field.json") { constrainedIntInfo() }
    }
    it("Can set a minimum and maximum double value") {
      openApiTest("min_max_double_field.json") { constrainedDoubleInfo() }
    }
    it("Can set an exclusive min and exclusive max integer value") {
      openApiTest("exclusive_min_max.json") { exclusiveMinMax() }
    }
    it("Can add a custom format to a string field") {
      openApiTest("formatted_param_type.json") { formattedParam() }
    }
    it("Can set a minimum and maximum length on a string field") {
      openApiTest("min_max_string.json") { minMaxString() }
    }
    it("Can set a custom regex pattern on a string field") {
      openApiTest("regex_string.json") { regexString() }
    }
    it("Can set a minimum and maximum item count on an array field") {
      openApiTest("min_max_array.json") { minMaxArray() }
    }
    it("Can set a unique items constraint on an array field") {
      openApiTest("unique_array.json") { uniqueArray() }
    }
    it("Can set a multiple-of constraint on an int field") {
      openApiTest("multiple_of_int.json") { multipleOfInt() }
    }
    it("Can set a multiple of constraint on an double field") {
      openApiTest("multiple_of_double.json") { multipleOfDouble() }
    }
    it("Can set a minimum and maximum number of properties on a free-form type") {
      openApiTest("min_max_free_form.json") { minMaxFreeForm() }
    }
  }
  describe("Free Form") {
    it("Can create a free-form field") {
      openApiTest("free_form_object.json") { freeFormObject() }
    }
  }
})
