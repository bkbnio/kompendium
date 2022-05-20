package io.bkbn.kompendium.core

import io.bkbn.kompendium.core.fixtures.TestHelpers.openApiTestAllSerializers
import io.bkbn.kompendium.core.util.TestModules.complexRequest
import io.bkbn.kompendium.core.util.TestModules.notarizedDelete
import io.bkbn.kompendium.core.util.TestModules.notarizedGet
import io.bkbn.kompendium.core.util.TestModules.notarizedHead
import io.bkbn.kompendium.core.util.TestModules.notarizedOptions
import io.bkbn.kompendium.core.util.TestModules.notarizedPatch
import io.bkbn.kompendium.core.util.TestModules.notarizedPost
import io.bkbn.kompendium.core.util.TestModules.notarizedPut
import io.kotest.core.spec.style.DescribeSpec

class KompendiumTest : DescribeSpec({
  describe("Notarized Open API Metadata Tests") {
    it("Can notarize a get request") {
      openApiTestAllSerializers("T0001__notarized_get.json") { notarizedGet() }
    }
    it("Can notarize a post request") {
      openApiTestAllSerializers("T0002__notarized_post.json") { notarizedPost() }
    }
    it("Can notarize a put request") {
      openApiTestAllSerializers("T0003__notarized_put.json") { notarizedPut() }
    }
    it("Can notarize a delete request") {
      openApiTestAllSerializers("T0004__notarized_delete.json") { notarizedDelete() }
    }
    it("Can notarize a patch request") {
      openApiTestAllSerializers("T0005__notarized_patch.json") { notarizedPatch() }
    }
    it("Can notarize a head request") {
      openApiTestAllSerializers("T0006__notarized_head.json") { notarizedHead() }
    }
    it("Can notarize an options request") {
      openApiTestAllSerializers("T0007__notarized_options.json") { notarizedOptions() }
    }
    it("Can notarize a complex type") {
      openApiTestAllSerializers("T0008__complex_type.json") { complexRequest() }
    }
  }
//    it("Can notarize a complex type") {
//      openApiTestAllSerializers("T0008__complex_type.json") { complexType() }
//    }
//    it("Can notarize primitives") {
//      openApiTestAllSerializers("notarized_primitives.json") { primitives() }
//    }
//    it("Can notarize a top level list response") {
//      openApiTestAllSerializers("response_list.json") { returnsList() }
//    }
//    it("Can notarize a route with non-required params") {
//      openApiTestAllSerializers("non_required_params.json") { nonRequiredParamsGet() }
//    }
//  }
//  describe("Notarized Ktor Functionality Tests") {
//    it("Can notarized a get request and return the expected result") {
//      apiFunctionalityTest("hey dude ‼️ congratz on the get request") { notarizedGetModule() }
//    }
//    it("Can notarize a post request and return the expected result") {
//      apiFunctionalityTest(
//        "hey dude ✌️ congratz on the post request",
//        httpMethod = HttpMethod.Post
//      ) { notarizedPostModule() }
//    }
//    it("Can notarize a put request and return the expected result") {
//      apiFunctionalityTest("hey pal 🌝 whatcha doin' here?", httpMethod = HttpMethod.Put) { notarizedPutModule() }
//    }
//    it("Can notarize a delete request and return the expected result") {
//      apiFunctionalityTest(
//        null,
//        httpMethod = HttpMethod.Delete,
//        expectedStatusCode = HttpStatusCode.NoContent
//      ) { notarizedDeleteModule() }
//    }
//    it("Can notarize the root route and return the expected result") {
//      apiFunctionalityTest("☎️🏠🌲", "/") { rootModule() }
//    }
//    it("Can notarize a trailing slash route and return the expected result") {
//      apiFunctionalityTest("🙀👾", "/test/") { trailingSlash() }
//    }
//  }
//  describe("Route Parsing") {
//    it("Can parse a simple path and store it under the expected route") {
//      openApiTestAllSerializers("path_parser.json") { pathParsingTestModule() }
//    }
//    it("Can notarize the root route") {
//      openApiTestAllSerializers("root_route.json") { rootModule() }
//    }
//    it("Can notarize a route under the root module without appending trailing slash") {
//      openApiTestAllSerializers("nested_under_root.json") { nestedUnderRootModule() }
//    }
//    it("Can notarize a route with a trailing slash") {
//      openApiTestAllSerializers("trailing_slash.json") { trailingSlash() }
//    }
//  }
//  describe("Exceptions") {
//    it("Can add an exception status code to a response") {
//      openApiTestAllSerializers("notarized_get_with_exception_response.json") { notarizedGetWithNotarizedException() }
//    }
//    it("Can support multiple response codes") {
//      openApiTestAllSerializers("notarized_get_with_multiple_exception_responses.json") { notarizedGetWithMultipleThrowables() }
//    }
//    it("Can add a polymorphic exception response") {
//      openApiTestAllSerializers("polymorphic_error_status_codes.json") { notarizedGetWithPolymorphicErrorResponse() }
//    }
//    it("Can add a generic exception response") {
//      openApiTestAllSerializers("generic_exception.json") { notarizedGetWithGenericErrorResponse() }
//    }
//  }
//  describe("Examples") {
//    it("Can generate example response and request bodies") {
//      openApiTestAllSerializers("example_req_and_resp.json") { withExamples() }
//    }
//    it("Can describe example parameters") {
//      openApiTestAllSerializers("example_parameters.json") { exampleParams() }
//    }
//  }
//  describe("Defaults") {
//    it("Can generate a default parameter values") {
//      openApiTestAllSerializers("query_with_default_parameter.json") { withDefaultParameter() }
//    }
//  }
//  describe("Required Fields") {
//    it("Marks a parameter required if there is no default and it is not marked nullable") {
//      openApiTestAllSerializers("required_param.json") { requiredParameter() }
//    }
//    it("Does not mark a parameter as required if a default value is provided") {
//      openApiTestAllSerializers("default_param.json") { defaultParameter() }
//    }
//    it("Does not mark a field as required if a default value is provided") {
//      openApiTestAllSerializers("default_field.json") { defaultField() }
//    }
//    it("Marks a field as nullable when expected") {
//      openApiTestAllSerializers("nullable_field.json") { nullableField() }
//    }
//  }
//  describe("Polymorphism and Generics") {
//    it("can generate a polymorphic response type") {
//      openApiTestAllSerializers("polymorphic_response.json") { polymorphicResponse() }
//    }
//    it("Can generate a collection with polymorphic response type") {
//      openApiTestAllSerializers("polymorphic_list_response.json") { polymorphicCollectionResponse() }
//    }
//    it("Can generate a map with a polymorphic response type") {
//      openApiTestAllSerializers("polymorphic_map_response.json") { polymorphicMapResponse() }
//    }
//    it("Can generate a polymorphic response from a sealed interface") {
//      openApiTestAllSerializers("sealed_interface_response.json") { polymorphicInterfaceResponse() }
//    }
//    it("Can generate a response type with a generic type") {
//      openApiTestAllSerializers("generic_response.json") { simpleGenericResponse() }
//    }
//    it("Can generate a polymorphic response type with generics") {
//      openApiTestAllSerializers("polymorphic_response_with_generics.json") { genericPolymorphicResponse() }
//    }
//    it("Can handle an absolutely psycho inheritance test") {
//      openApiTestAllSerializers("crazy_polymorphic_example.json") { genericPolymorphicResponseMultipleImpls() }
//    }
//  }
//  describe("Miscellaneous") {
//    it("Can generate the necessary ReDoc home page") {
//      apiFunctionalityTest(getFileSnapshot("redoc.html"), "/docs") { returnsList() }
//    }
//    it("Can add an operation id to a notarized route") {
//      openApiTestAllSerializers("notarized_get_with_operation_id.json") { withOperationId() }
//    }
//    it("Can add an undeclared field") {
//      openApiTestAllSerializers("undeclared_field.json") { undeclaredType() }
//    }
//    it("Can add a custom header parameter with a name override") {
//      openApiTestAllSerializers("override_parameter_name.json") { headerParameter() }
//    }
//    it("Can override field values via annotation") {
//      openApiTestAllSerializers("field_override.json") { overrideFieldInfo() }
//    }
//    it("Can serialize a recursive type") {
//      openApiTestAllSerializers("simple_recursive.json") { simpleRecursive() }
//    }
//    it("Nullable fields do not lead to doom") {
//      openApiTestAllSerializers("nullable_fields.json") { nullableNestedObject() }
//    }
//    it("Can have a nullable enum as a member field") {
//      openApiTestAllSerializers("nullable_enum_field.json") { nullableEnumField() }
//    }
//  }
//  describe("Constraints") {
//    it("Can set a minimum and maximum integer value") {
//      openApiTestAllSerializers("min_max_int_field.json") { constrainedIntInfo() }
//    }
//    it("Can set a minimum and maximum double value") {
//      openApiTestAllSerializers("min_max_double_field.json") { constrainedDoubleInfo() }
//    }
//    it("Can set an exclusive min and exclusive max integer value") {
//      openApiTestAllSerializers("exclusive_min_max.json") { exclusiveMinMax() }
//    }
//    it("Can add a custom format to a string field") {
//      openApiTestAllSerializers("formatted_param_type.json") { formattedParam() }
//    }
//    it("Can set a minimum and maximum length on a string field") {
//      openApiTestAllSerializers("min_max_string.json") { minMaxString() }
//    }
//    it("Can set a custom regex pattern on a string field") {
//      openApiTestAllSerializers("regex_string.json") { regexString() }
//    }
//    it("Can set a minimum and maximum item count on an array field") {
//      openApiTestAllSerializers("min_max_array.json") { minMaxArray() }
//    }
//    it("Can set a unique items constraint on an array field") {
//      openApiTestAllSerializers("unique_array.json") { uniqueArray() }
//    }
//    it("Can set a multiple-of constraint on an int field") {
//      openApiTestAllSerializers("multiple_of_int.json") { multipleOfInt() }
//    }
//    it("Can set a multiple of constraint on an double field") {
//      openApiTestAllSerializers("multiple_of_double.json") { multipleOfDouble() }
//    }
//    it("Can set a minimum and maximum number of properties on a free-form type") {
//      openApiTestAllSerializers("min_max_free_form.json") { minMaxFreeForm() }
//    }
//    it("Can add a custom format to a collection type") {
//      openApiTestAllSerializers("formatted_array_item_type.json") { formattedType() }
//    }
//  }
//  describe("Formats") {
//    it("Can set a format on a simple type schema") {
//      openApiTestAllSerializers("formatted_date_time_string.json", { dateTimeString() }) {
//        addCustomTypeSchema(Instant::class, SimpleSchema("string", format = "date-time"))
//      }
//    }
//    it("Can set a format on formatted type schema") {
//      openApiTestAllSerializers("formatted_date_time_string.json", { dateTimeString() }) {
//        addCustomTypeSchema(Instant::class, FormattedSchema("date-time", "string"))
//      }
//    }
//    it("Can bypass a format on a simple type schema") {
//      openApiTestAllSerializers("formatted_no_format_string.json", { dateTimeString() }) {
//        addCustomTypeSchema(Instant::class, SimpleSchema("string"))
//      }
//    }
//  }
//  describe("Free Form") {
//    it("Can create a free-form field") {
//      openApiTestAllSerializers("free_form_field.json") { freeFormField() }
//    }
//    it("Can create a top-level free form object") {
//      openApiTestAllSerializers("free_form_object.json") { freeFormObject() }
//    }
//  }
//  describe("Serialization overrides") {
//    it("Can override the jackson serializer") {
//      withTestApplication({
//        install(Kompendium) {
//          spec = defaultSpec()
//          openApiJson = { spec ->
//            val om = ObjectMapper().apply {
//              setSerializationInclusion(JsonInclude.Include.NON_NULL)
//            }
//            route("/openapi.json") {
//              get {
//                call.respondText { om.writeValueAsString(spec) }
//              }
//            }
//          }
//        }
//        install(ContentNegotiation) {
//          jackson(ContentType.Application.Json)
//        }
//        docs()
//        withExamples()
//      }) {
//        compareOpenAPISpec("example_req_and_resp.json")
//      }
//    }
//    it("Can override the kotlinx serializer") {
//      withTestApplication({
//        install(Kompendium) {
//          spec = defaultSpec()
//          openApiJson = { spec ->
//            val om = ObjectMapper().apply {
//              setSerializationInclusion(JsonInclude.Include.NON_NULL)
//            }
//            route("/openapi.json") {
//              get {
//                val customSerializer = Json {
//                  serializersModule = KompendiumSerializersModule.module
//                  encodeDefaults = true
//                  explicitNulls = false
//                }
//                call.respondText { customSerializer.encodeToString(spec) }
//              }
//            }
//          }
//        }
//        install(ContentNegotiation) {
//          json()
//        }
//        docs()
//        withExamples()
//      }) {
//        compareOpenAPISpec("example_req_and_resp.json")
//      }
//    }
//  }
})
