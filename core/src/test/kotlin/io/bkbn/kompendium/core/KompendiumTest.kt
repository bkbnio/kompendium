package io.bkbn.kompendium.core

import io.bkbn.kompendium.core.fixtures.TestHelpers.openApiTestAllSerializers
import io.bkbn.kompendium.core.util.complexRequest
import io.bkbn.kompendium.core.util.customFieldNameResponse
import io.bkbn.kompendium.core.util.defaultField
import io.bkbn.kompendium.core.util.fieldOutsideConstructor
import io.bkbn.kompendium.core.util.headerParameter
import io.bkbn.kompendium.core.util.ignoredFieldsResponse
import io.bkbn.kompendium.core.util.nestedTypeName
import io.bkbn.kompendium.core.util.nonRequiredParam
import io.bkbn.kompendium.core.util.nonRequiredParams
import io.bkbn.kompendium.core.util.notarizedDelete
import io.bkbn.kompendium.core.util.notarizedGet
import io.bkbn.kompendium.core.util.notarizedHead
import io.bkbn.kompendium.core.util.notarizedOptions
import io.bkbn.kompendium.core.util.notarizedPatch
import io.bkbn.kompendium.core.util.notarizedPost
import io.bkbn.kompendium.core.util.notarizedPut
import io.bkbn.kompendium.core.util.nullableEnumField
import io.bkbn.kompendium.core.util.nullableField
import io.bkbn.kompendium.core.util.nullableNestedObject
import io.bkbn.kompendium.core.util.nullableReference
import io.bkbn.kompendium.core.util.overrideMediaTypes
import io.bkbn.kompendium.core.util.postNoReqBody
import io.bkbn.kompendium.core.util.primitives
import io.bkbn.kompendium.core.util.requiredParams
import io.bkbn.kompendium.core.util.responseHeaders
import io.bkbn.kompendium.core.util.returnsEnumList
import io.bkbn.kompendium.core.util.returnsList
import io.bkbn.kompendium.core.util.samePathDifferentMethodsAndAuth
import io.bkbn.kompendium.core.util.simpleRecursive
import io.bkbn.kompendium.core.util.topLevelNullable
import io.bkbn.kompendium.core.util.unbackedFieldsResponse
import io.bkbn.kompendium.core.util.withOperationId
import io.bkbn.kompendium.oas.component.Components
import io.bkbn.kompendium.oas.security.BasicAuth
import io.bkbn.kompendium.oas.serialization.KompendiumSerializersModule
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URI

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
    it("Can override media types") {
      openApiTestAllSerializers("T0052__override_media_types.json") { overrideMediaTypes() }
    }
    it("Can support a post request with no request body") {
      openApiTestAllSerializers("T0065__post_no_req_body.json") { postNoReqBody() }
    }
    it("Can notarize a route with response headers") {
      openApiTestAllSerializers("T0066__notarized_get_with_response_headers.json") { responseHeaders() }
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
  describe("Custom Serializable Reader tests") {
    it("Can support ignoring fields") {
      openApiTestAllSerializers("T0048__ignored_property.json") { ignoredFieldsResponse() }
    }
    it("Can support un-backed fields") {
      openApiTestAllSerializers("T0049__unbacked_property.json") { unbackedFieldsResponse() }
    }
    it("Can support custom named fields") {
      openApiTestAllSerializers("T0050__custom_named_property.json") { customFieldNameResponse() }
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
    it("Can have a list of enums as a field") {
      openApiTestAllSerializers("T0076__list_of_enums.json") { returnsEnumList() }
    }
    it("Can have a nullable reference without impacting base type") {
      openApiTestAllSerializers("T0041__nullable_reference.json") { nullableReference() }
    }
    it("Can handle nested type names") {
      openApiTestAllSerializers("T0044__nested_type_name.json") { nestedTypeName() }
    }
    it("Can handle top level nullable types") {
      openApiTestAllSerializers("T0051__top_level_nullable.json") { topLevelNullable() }
    }
    it("Can handle multiple registrations for different methods with the same path and different auth") {
      openApiTestAllSerializers(
        "T0053__same_path_different_methods_and_auth.json",
        applicationSetup = {
          install(Authentication) {
            basic("basic") {
              realm = "Ktor Server"
              validate { UserIdPrincipal("Placeholder") }
            }
          }
        },
        specOverrides = {
          this.copy(
            components = Components(
              securitySchemes = mutableMapOf(
                "basic" to BasicAuth()
              )
            )
          )
        }
      ) { samePathDifferentMethodsAndAuth() }
    }
    it("Can generate paths without application root-path") {
      openApiTestAllSerializers(
        "T0054__app_with_rootpath.json",
        applicationSetup = {
          rootPath = "/example"
        },
        specOverrides = {
          copy(
            servers = servers.map { it.copy(url = URI("${it.url}/example")) }.toMutableList()
          )
        },
        serverConfigSetup = {
          rootPath = "/example"
        }
      ) { notarizedGet() }
    }
    it("Can apply a custom serialization strategy to the openapi document") {
      val customJsonEncoder = Json {
        serializersModule = KompendiumSerializersModule.module
        encodeDefaults = true
        explicitNulls = false
      }
      openApiTestAllSerializers(
        snapshotName = "T0072__custom_serialization_strategy.json",
        notarizedApplicationConfigOverrides = {
          specRoute = { spec, routing ->
            routing.route("/openapi.json") {
              get {
                call.response.headers.append("Content-Type", "application/json")
                call.respondText { customJsonEncoder.encodeToString(spec) }
              }
            }
          }
        },
        contentNegotiation = {
          json(
            Json {
              encodeDefaults = true
              explicitNulls = true
            }
          )
        }
      ) { notarizedGet() }
    }
    it("Can serialize a data class with a field outside of the constructor") {
      openApiTestAllSerializers("T0073__data_class_with_field_outside_constructor.json") { fieldOutsideConstructor() }
    }
  }
})
