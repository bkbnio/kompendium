package io.bkbn.kompendium.core

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import io.bkbn.kompendium.core.fixtures.TestHelpers.apiFunctionalityTest
import io.bkbn.kompendium.core.fixtures.TestHelpers.compareOpenAPISpec
import io.bkbn.kompendium.core.fixtures.TestHelpers.getFileSnapshot
import io.bkbn.kompendium.core.fixtures.TestHelpers.openApiTestAllSerializers
import io.bkbn.kompendium.core.fixtures.TestSpecs.defaultSpec
import io.bkbn.kompendium.core.fixtures.docs
import io.bkbn.kompendium.core.util.complexType
import io.bkbn.kompendium.core.util.constrainedDoubleInfo
import io.bkbn.kompendium.core.util.constrainedIntInfo
import io.bkbn.kompendium.core.util.dateTimeString
import io.bkbn.kompendium.core.util.defaultField
import io.bkbn.kompendium.core.util.defaultParameter
import io.bkbn.kompendium.core.util.exampleParams
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
import io.bkbn.kompendium.core.util.nullableNestedObject
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
import io.bkbn.kompendium.core.util.simpleRecursive
import io.bkbn.kompendium.core.util.trailingSlash
import io.bkbn.kompendium.core.util.undeclaredType
import io.bkbn.kompendium.core.util.uniqueArray
import io.bkbn.kompendium.core.util.withDefaultParameter
import io.bkbn.kompendium.core.util.withExamples
import io.bkbn.kompendium.core.util.withOperationId
import io.bkbn.kompendium.oas.schema.FormattedSchema
import io.bkbn.kompendium.oas.schema.SimpleSchema
import io.bkbn.kompendium.oas.serialization.KompendiumSerializersModule
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.serialization.json
import io.ktor.server.testing.withTestApplication
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant

class KompendiumTest : DescribeSpec({
  describe("Notarized Open API Metadata Tests") {
    it("Can notarize a get request") {
      openApiTestAllSerializers("notarized_get.json") { notarizedGetModule() }
    }
    it("Can notarize a post request") {
      openApiTestAllSerializers("notarized_post.json") { notarizedPostModule() }
    }
    it("Can notarize a put request") {
      openApiTestAllSerializers("notarized_put.json") { notarizedPutModule() }
    }
    it("Can notarize a delete request") {
      openApiTestAllSerializers("notarized_delete.json") { notarizedDeleteModule() }
    }
    it("Can notarize a patch request") {
      openApiTestAllSerializers("notarized_patch.json") { notarizedPatchModule() }
    }
    it("Can notarize a head request") {
      openApiTestAllSerializers("notarized_head.json") { notarizedHeadModule() }
    }
    it("Can notarize an options request") {
      openApiTestAllSerializers("notarized_options.json") { notarizedOptionsModule() }
    }
    it("Can notarize a complex type") {
      openApiTestAllSerializers("complex_type.json") { complexType() }
    }
    it("Can notarize primitives") {
      openApiTestAllSerializers("notarized_primitives.json") { primitives() }
    }
    it("Can notarize a top level list response") {
      openApiTestAllSerializers("response_list.json") { returnsList() }
    }
    it("Can notarize a route with non-required params") {
      openApiTestAllSerializers("non_required_params.json") { nonRequiredParamsGet() }
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
      openApiTestAllSerializers("path_parser.json") { pathParsingTestModule() }
    }
    it("Can notarize the root route") {
      openApiTestAllSerializers("root_route.json") { rootModule() }
    }
    it("Can notarize a route under the root module without appending trailing slash") {
      openApiTestAllSerializers("nested_under_root.json") { nestedUnderRootModule() }
    }
    it("Can notarize a route with a trailing slash") {
      openApiTestAllSerializers("trailing_slash.json") { trailingSlash() }
    }
  }
  describe("Exceptions") {
    it("Can add an exception status code to a response") {
      openApiTestAllSerializers("notarized_get_with_exception_response.json") { notarizedGetWithNotarizedException() }
    }
    it("Can support multiple response codes") {
      openApiTestAllSerializers("notarized_get_with_multiple_exception_responses.json") { notarizedGetWithMultipleThrowables() }
    }
    it("Can add a polymorphic exception response") {
      openApiTestAllSerializers("polymorphic_error_status_codes.json") { notarizedGetWithPolymorphicErrorResponse() }
    }
    it("Can add a generic exception response") {
      openApiTestAllSerializers("generic_exception.json") { notarizedGetWithGenericErrorResponse() }
    }
  }
  describe("Examples") {
    it("Can generate example response and request bodies") {
      openApiTestAllSerializers("example_req_and_resp.json") { withExamples() }
    }
    it("Can describe example parameters") {
      openApiTestAllSerializers("example_parameters.json") { exampleParams() }
    }
  }
  describe("Defaults") {
    it("Can generate a default parameter values") {
      openApiTestAllSerializers("query_with_default_parameter.json") { withDefaultParameter() }
    }
  }
  describe("Required Fields") {
    it("Marks a parameter required if there is no default and it is not marked nullable") {
      openApiTestAllSerializers("required_param.json") { requiredParameter() }
    }
    it("Does not mark a parameter as required if a default value is provided") {
      openApiTestAllSerializers("default_param.json") { defaultParameter() }
    }
    it("Does not mark a field as required if a default value is provided") {
      openApiTestAllSerializers("default_field.json") { defaultField() }
    }
    it("Marks a field as nullable when expected") {
      openApiTestAllSerializers("nullable_field.json") { nullableField() }
    }
  }
  describe("Polymorphism and Generics") {
    it("can generate a polymorphic response type") {
      openApiTestAllSerializers("polymorphic_response.json") { polymorphicResponse() }
    }
    it("Can generate a collection with polymorphic response type") {
      openApiTestAllSerializers("polymorphic_list_response.json") { polymorphicCollectionResponse() }
    }
    it("Can generate a map with a polymorphic response type") {
      openApiTestAllSerializers("polymorphic_map_response.json") { polymorphicMapResponse() }
    }
    it("Can generate a polymorphic response from a sealed interface") {
      openApiTestAllSerializers("sealed_interface_response.json") { polymorphicInterfaceResponse() }
    }
    it("Can generate a response type with a generic type") {
      openApiTestAllSerializers("generic_response.json") { simpleGenericResponse() }
    }
    it("Can generate a polymorphic response type with generics") {
      openApiTestAllSerializers("polymorphic_response_with_generics.json") { genericPolymorphicResponse() }
    }
    it("Can handle an absolutely psycho inheritance test") {
      openApiTestAllSerializers("crazy_polymorphic_example.json") { genericPolymorphicResponseMultipleImpls() }
    }
  }
  describe("Miscellaneous") {
    it("Can generate the necessary ReDoc home page") {
      apiFunctionalityTest(getFileSnapshot("redoc.html"), "/docs") { returnsList() }
    }
    it("Can add an operation id to a notarized route") {
      openApiTestAllSerializers("notarized_get_with_operation_id.json") { withOperationId() }
    }
    it("Can add an undeclared field") {
      openApiTestAllSerializers("undeclared_field.json") { undeclaredType() }
    }
    it("Can add a custom header parameter with a name override") {
      openApiTestAllSerializers("override_parameter_name.json") { headerParameter() }
    }
    it("Can override field values via annotation") {
      openApiTestAllSerializers("field_override.json") { overrideFieldInfo() }
    }
    it("Can serialize a recursive type") {
      openApiTestAllSerializers("simple_recursive.json") { simpleRecursive() }
    }
    it("Nullable fields do not lead to doom") {
      openApiTestAllSerializers("nullable_fields.json") { nullableNestedObject() }
    }
  }
  describe("Constraints") {
    it("Can set a minimum and maximum integer value") {
      openApiTestAllSerializers("min_max_int_field.json") { constrainedIntInfo() }
    }
    it("Can set a minimum and maximum double value") {
      openApiTestAllSerializers("min_max_double_field.json") { constrainedDoubleInfo() }
    }
    it("Can set an exclusive min and exclusive max integer value") {
      openApiTestAllSerializers("exclusive_min_max.json") { exclusiveMinMax() }
    }
    it("Can add a custom format to a string field") {
      openApiTestAllSerializers("formatted_param_type.json") { formattedParam() }
    }
    it("Can set a minimum and maximum length on a string field") {
      openApiTestAllSerializers("min_max_string.json") { minMaxString() }
    }
    it("Can set a custom regex pattern on a string field") {
      openApiTestAllSerializers("regex_string.json") { regexString() }
    }
    it("Can set a minimum and maximum item count on an array field") {
      openApiTestAllSerializers("min_max_array.json") { minMaxArray() }
    }
    it("Can set a unique items constraint on an array field") {
      openApiTestAllSerializers("unique_array.json") { uniqueArray() }
    }
    it("Can set a multiple-of constraint on an int field") {
      openApiTestAllSerializers("multiple_of_int.json") { multipleOfInt() }
    }
    it("Can set a multiple of constraint on an double field") {
      openApiTestAllSerializers("multiple_of_double.json") { multipleOfDouble() }
    }
    it("Can set a minimum and maximum number of properties on a free-form type") {
      openApiTestAllSerializers("min_max_free_form.json") { minMaxFreeForm() }
    }
  }
  describe("Formats") {
    it("Can set a format on a simple type schema") {
      openApiTestAllSerializers("formatted_date_time_string.json", { dateTimeString() }) {
        addCustomTypeSchema(Instant::class, SimpleSchema("string", format = "date-time"))
      }
    }
    it("Can set a format on formatted type schema") {
      openApiTestAllSerializers("formatted_date_time_string.json", { dateTimeString() }) {
        addCustomTypeSchema(Instant::class, FormattedSchema("date-time", "string"))
      }
    }
    it("Can bypass a format on a simple type schema") {
      openApiTestAllSerializers("formatted_no_format_string.json", { dateTimeString() }) {
        addCustomTypeSchema(Instant::class, SimpleSchema("string"))
      }
    }
  }
  describe("Free Form") {
    it("Can create a free-form field") {
      openApiTestAllSerializers("free_form_object.json") { freeFormObject() }
    }
  }
  describe("Serialization overrides") {
    it("Can override the jackson serializer") {
      withTestApplication({
        install(Kompendium) {
          spec = defaultSpec()
          openApiJson = { spec ->
            val om = ObjectMapper().apply {
              setSerializationInclusion(JsonInclude.Include.NON_NULL)
            }
            route("/openapi.json") {
              get {
                call.respondText { om.writeValueAsString(spec) }
              }
            }
          }
        }
        install(ContentNegotiation) {
          jackson(ContentType.Application.Json)
        }
        docs()
        withExamples()
      }) {
        compareOpenAPISpec("example_req_and_resp.json")
      }
    }
    it("Can override the kotlinx serializer") {
      withTestApplication({
        install(Kompendium) {
          spec = defaultSpec()
          openApiJson = { spec ->
            val om = ObjectMapper().apply {
              setSerializationInclusion(JsonInclude.Include.NON_NULL)
            }
            route("/openapi.json") {
              get {
                val customSerializer = Json {
                  serializersModule = KompendiumSerializersModule.module
                  encodeDefaults = true
                  explicitNulls = false
                }
                call.respondText { customSerializer.encodeToString(spec) }
              }
            }
          }
        }
        install(ContentNegotiation) {
          json()
        }
        docs()
        withExamples()
      }) {
        compareOpenAPISpec("example_req_and_resp.json")
      }
    }
  }
})
