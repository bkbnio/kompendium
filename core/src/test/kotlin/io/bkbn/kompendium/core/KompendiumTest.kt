package io.bkbn.kompendium.core

import io.bkbn.kompendium.core.fixtures.TestHelpers.openApiTestAllSerializers
import io.bkbn.kompendium.core.util.TestModules.complexRequest
import io.bkbn.kompendium.core.util.TestModules.dateTimeString
import io.bkbn.kompendium.core.util.TestModules.defaultField
import io.bkbn.kompendium.core.util.TestModules.defaultParameter
import io.bkbn.kompendium.core.util.TestModules.exampleParams
import io.bkbn.kompendium.core.util.TestModules.nestedUnderRoot
import io.bkbn.kompendium.core.util.TestModules.nonRequiredParams
import io.bkbn.kompendium.core.util.TestModules.notarizedDelete
import io.bkbn.kompendium.core.util.TestModules.notarizedGet
import io.bkbn.kompendium.core.util.TestModules.singleException
import io.bkbn.kompendium.core.util.TestModules.genericException
import io.bkbn.kompendium.core.util.TestModules.genericPolymorphicResponse
import io.bkbn.kompendium.core.util.TestModules.genericPolymorphicResponseMultipleImpls
import io.bkbn.kompendium.core.util.TestModules.gnarlyGenericResponse
import io.bkbn.kompendium.core.util.TestModules.headerParameter
import io.bkbn.kompendium.core.util.TestModules.multipleExceptions
import io.bkbn.kompendium.core.util.TestModules.nestedGenericCollection
import io.bkbn.kompendium.core.util.TestModules.nestedGenericMultipleParamsCollection
import io.bkbn.kompendium.core.util.TestModules.nestedGenericResponse
import io.bkbn.kompendium.core.util.TestModules.nonRequiredParam
import io.bkbn.kompendium.core.util.TestModules.polymorphicException
import io.bkbn.kompendium.core.util.TestModules.notarizedHead
import io.bkbn.kompendium.core.util.TestModules.notarizedOptions
import io.bkbn.kompendium.core.util.TestModules.notarizedPatch
import io.bkbn.kompendium.core.util.TestModules.notarizedPost
import io.bkbn.kompendium.core.util.TestModules.notarizedPut
import io.bkbn.kompendium.core.util.TestModules.nullableEnumField
import io.bkbn.kompendium.core.util.TestModules.nullableField
import io.bkbn.kompendium.core.util.TestModules.nullableNestedObject
import io.bkbn.kompendium.core.util.TestModules.nullableReference
import io.bkbn.kompendium.core.util.TestModules.polymorphicCollectionResponse
import io.bkbn.kompendium.core.util.TestModules.polymorphicMapResponse
import io.bkbn.kompendium.core.util.TestModules.polymorphicResponse
import io.bkbn.kompendium.core.util.TestModules.primitives
import io.bkbn.kompendium.core.util.TestModules.reqRespExamples
import io.bkbn.kompendium.core.util.TestModules.requiredParams
import io.bkbn.kompendium.core.util.TestModules.returnsList
import io.bkbn.kompendium.core.util.TestModules.rootRoute
import io.bkbn.kompendium.core.util.TestModules.simpleGenericResponse
import io.bkbn.kompendium.core.util.TestModules.simplePathParsing
import io.bkbn.kompendium.core.util.TestModules.simpleRecursive
import io.bkbn.kompendium.core.util.TestModules.trailingSlash
import io.bkbn.kompendium.core.util.TestModules.withOperationId
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.kotest.core.spec.style.DescribeSpec
import kotlin.reflect.typeOf
import java.time.Instant

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
    it("Can notarize primitives") {
      openApiTestAllSerializers("T0009__notarized_primitives.json") { primitives() }
    }
    it("Can notarize a top level list response") {
      openApiTestAllSerializers("T0010__response_list.json") { returnsList() }
    }
    it("Can notarize a route with non-required params") {
      openApiTestAllSerializers("T0011__non_required_params.json") { nonRequiredParams() }
    }
  }
  describe("Route Parsing") {
    it("Can parse a simple path and store it under the expected route") {
      openApiTestAllSerializers("T0012__path_parser.json") { simplePathParsing() }
    }
    it("Can notarize the root route") {
      openApiTestAllSerializers("T0013__root_route.json") { rootRoute() }
    }
    it("Can notarize a route under the root module without appending trailing slash") {
      openApiTestAllSerializers("T0014__nested_under_root.json") { nestedUnderRoot() }
    }
    it("Can notarize a route with a trailing slash") {
      openApiTestAllSerializers("T0015__trailing_slash.json") { trailingSlash() }
    }
  }
  describe("Exceptions") {
    it("Can add an exception status code to a response") {
      openApiTestAllSerializers("T0016__notarized_get_with_exception_response.json") { singleException() }
    }
    it("Can support multiple response codes") {
      openApiTestAllSerializers("T0017__notarized_get_with_multiple_exception_responses.json") { multipleExceptions() }
    }
    it("Can add a polymorphic exception response") {
      openApiTestAllSerializers("T0018__polymorphic_error_status_codes.json") { polymorphicException() }
    }
    it("Can add a generic exception response") {
      openApiTestAllSerializers("T0019__generic_exception.json") { genericException() }
    }
  }
  describe("Examples") {
    it("Can generate example response and request bodies") {
      openApiTestAllSerializers("T0020__example_req_and_resp.json") { reqRespExamples() }
    }
    it("Can describe example parameters") {
      openApiTestAllSerializers("T0021__example_parameters.json") { exampleParams() }
    }
  }
  describe("Defaults") {
    it("Can generate a default parameter value") {
      openApiTestAllSerializers("T0022__query_with_default_parameter.json") { defaultParameter() }
    }
  }
  describe("Required Fields") {
    it("Marks a parameter as required if there is no default and it is not marked nullable") {
      openApiTestAllSerializers("T0023__required_param.json") { requiredParams() }
    }
    it("Can mark a parameter as not required") {
      openApiTestAllSerializers("T0024__non_required_param.json") { nonRequiredParam() }
    }
    it("Does not mark a field as required if a default value is provided") {
      openApiTestAllSerializers("T0025__default_field.json") { defaultField() }
    }
    it("Does not mark a nullable field as required") {
      openApiTestAllSerializers("T0026__nullable_field.json") { nullableField() }
    }
  }
  describe("Polymorphism and Generics") {
    it("can generate a polymorphic response type") {
      openApiTestAllSerializers("T0027__polymorphic_response.json") { polymorphicResponse() }
    }
    it("Can generate a collection with polymorphic response type") {
      openApiTestAllSerializers("T0028__polymorphic_list_response.json") { polymorphicCollectionResponse() }
    }
    it("Can generate a map with a polymorphic response type") {
      openApiTestAllSerializers("T0029__polymorphic_map_response.json") { polymorphicMapResponse() }
    }
    it("Can generate a response type with a generic type") {
      openApiTestAllSerializers("T0030__simple_generic_response.json") { simpleGenericResponse() }
    }
    it("Can generate a response type with a nested generic type") {
      openApiTestAllSerializers("T0031__nested_generic_response.json") { nestedGenericResponse() }
    }
    it("Can generate a polymorphic response type with generics") {
      openApiTestAllSerializers("T0032__polymorphic_response_with_generics.json") { genericPolymorphicResponse() }
    }
    it("Can handle an absolutely psycho inheritance test") {
      openApiTestAllSerializers("T0033__crazy_polymorphic_example.json") { genericPolymorphicResponseMultipleImpls() }
    }
    it("Can support nested generic collections") {
      openApiTestAllSerializers("T0039__nested_generic_collection.json") { nestedGenericCollection() }
    }
    it("Can support nested generics with multiple type parameters") {
      openApiTestAllSerializers("T0040__nested_generic_multiple_type_params.json") { nestedGenericMultipleParamsCollection() }
    }
    it("Can handle a really gnarly generic example") {
      openApiTestAllSerializers("T0043__gnarly_generic_example.json") { gnarlyGenericResponse() }
    }
  }
  describe("Miscellaneous") {
    xit("Can generate the necessary ReDoc home page") {
      // TODO apiFunctionalityTest(getFileSnapshot("redoc.html"), "/docs") { returnsList() }
    }
    it("Can add an operation id to a notarized route") {
      openApiTestAllSerializers("T0034__notarized_get_with_operation_id.json") { withOperationId() }
    }
    xit("Can add an undeclared field") {
      // TODO openApiTestAllSerializers("undeclared_field.json") { undeclaredType() }
    }
    it("Can add a custom header parameter with a name override") {
      openApiTestAllSerializers("T0035__override_parameter_name.json") { headerParameter() }
    }
    xit("Can override field name") {
      // TODO Assess strategies here
    }
    it("Can serialize a recursive type") {
       openApiTestAllSerializers("T0042__simple_recursive.json") { simpleRecursive() }
    }
    it("Nullable fields do not lead to doom") {
      openApiTestAllSerializers("T0036__nullable_fields.json") { nullableNestedObject() }
    }
    it("Can have a nullable enum as a member field") {
      openApiTestAllSerializers("T0037__nullable_enum_field.json") { nullableEnumField() }
    }
    it("Can have a nullable reference without impacting base type") {
      openApiTestAllSerializers("T0041__nullable_reference.json") { nullableReference() }
    }
  }
  describe("Constraints") {
    // TODO Assess strategies here
  }
  describe("Formats") {
    it("Can set a format for a simple type schema") {
      openApiTestAllSerializers(
        snapshotName = "T0038__formatted_date_time_string.json",
        customTypes = mapOf(typeOf<Instant>() to TypeDefinition(type = "string", format = "date"))
      ) { dateTimeString() }
    }
  }
  describe("Free Form") {
    // todo Assess strategies here
  }
})
