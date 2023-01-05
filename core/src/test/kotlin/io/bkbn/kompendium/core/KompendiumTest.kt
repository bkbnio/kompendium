package io.bkbn.kompendium.core

import dev.forst.ktor.apikey.apiKey
import io.bkbn.kompendium.core.fixtures.TestHelpers.openApiTestAllSerializers
import io.bkbn.kompendium.core.util.complexRequest
import io.bkbn.kompendium.core.util.customAuthConfig
import io.bkbn.kompendium.core.util.customFieldNameResponse
import io.bkbn.kompendium.core.util.dateTimeString
import io.bkbn.kompendium.core.util.defaultAuthConfig
import io.bkbn.kompendium.core.util.defaultField
import io.bkbn.kompendium.core.util.defaultParameter
import io.bkbn.kompendium.core.util.enrichedComplexGenericType
import io.bkbn.kompendium.core.util.enrichedNestedCollection
import io.bkbn.kompendium.core.util.enrichedSimpleRequest
import io.bkbn.kompendium.core.util.enrichedSimpleResponse
import io.bkbn.kompendium.core.util.exampleParams
import io.bkbn.kompendium.core.util.genericException
import io.bkbn.kompendium.core.util.genericPolymorphicResponse
import io.bkbn.kompendium.core.util.genericPolymorphicResponseMultipleImpls
import io.bkbn.kompendium.core.util.gnarlyGenericResponse
import io.bkbn.kompendium.core.util.headerParameter
import io.bkbn.kompendium.core.util.ignoredFieldsResponse
import io.bkbn.kompendium.core.util.multipleAuthStrategies
import io.bkbn.kompendium.core.util.multipleExceptions
import io.bkbn.kompendium.core.util.nestedGenericCollection
import io.bkbn.kompendium.core.util.nestedGenericMultipleParamsCollection
import io.bkbn.kompendium.core.util.nestedGenericResponse
import io.bkbn.kompendium.core.util.nestedTypeName
import io.bkbn.kompendium.core.util.nestedUnderRoot
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
import io.bkbn.kompendium.core.util.polymorphicCollectionResponse
import io.bkbn.kompendium.core.util.polymorphicException
import io.bkbn.kompendium.core.util.polymorphicMapResponse
import io.bkbn.kompendium.core.util.polymorphicResponse
import io.bkbn.kompendium.core.util.primitives
import io.bkbn.kompendium.core.util.reqRespExamples
import io.bkbn.kompendium.core.util.requiredParams
import io.bkbn.kompendium.core.util.returnsList
import io.bkbn.kompendium.core.util.rootRoute
import io.bkbn.kompendium.core.util.samePathDifferentMethodsAndAuth
import io.bkbn.kompendium.core.util.samePathSameMethod
import io.bkbn.kompendium.core.util.simpleGenericResponse
import io.bkbn.kompendium.core.util.simplePathParsing
import io.bkbn.kompendium.core.util.simpleRecursive
import io.bkbn.kompendium.core.util.singleException
import io.bkbn.kompendium.core.util.topLevelNullable
import io.bkbn.kompendium.core.util.trailingSlash
import io.bkbn.kompendium.core.util.unbackedFieldsResponse
import io.bkbn.kompendium.core.util.withOperationId
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.bkbn.kompendium.json.schema.exception.UnknownSchemaException
import io.bkbn.kompendium.oas.component.Components
import io.bkbn.kompendium.oas.security.ApiKeyAuth
import io.bkbn.kompendium.oas.security.BasicAuth
import io.bkbn.kompendium.oas.security.BearerAuth
import io.bkbn.kompendium.oas.security.OAuth
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.should
import io.kotest.matchers.string.startWith
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.http.HttpMethod
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.OAuthServerSettings
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.auth.oauth
import java.net.URI
import java.time.Instant
import kotlin.reflect.typeOf

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
      openApiTestAllSerializers("T0040__nested_generic_multiple_type_params.json") {
        nestedGenericMultipleParamsCollection()
      }
    }
    it("Can handle a really gnarly generic example") {
      openApiTestAllSerializers("T0043__gnarly_generic_example.json") { gnarlyGenericResponse() }
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
        applicationEnvironmentBuilder = {
          rootPath = "/example"
        },
        specOverrides = {
          copy(
            servers = servers.map { it.copy(url = URI("${it.url}/example")) }.toMutableList()
          )
        }
      ) { notarizedGet() }
    }
  }
  describe("Error Handling") {
    it("Throws a clear exception when an unidentified type is encountered") {
      val exception = shouldThrow<UnknownSchemaException> { openApiTestAllSerializers("") { dateTimeString() } }
      exception.message should startWith("An unknown type was encountered: class java.time.Instant")
    }
    it("Throws an exception when same method for same path has been previously registered") {
      val exception = shouldThrow<IllegalArgumentException> {
        openApiTestAllSerializers(
          snapshotName = "",
          applicationSetup = {
            install(Authentication) {
              basic("basic") {
                realm = "Ktor Server"
                validate { UserIdPrincipal("Placeholder") }
              }
            }
          },
        ) {
          samePathSameMethod()
        }
      }
      exception.message should startWith("A route has already been registered for path: /test/{a} and method: GET")
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
  describe("Authentication") {
    it("Can add a default auth config by default") {
      openApiTestAllSerializers(
        snapshotName = "T0045__default_auth_config.json",
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
      ) { defaultAuthConfig() }
    }
    it("Can provide custom auth config with proper scopes") {
      openApiTestAllSerializers(
        snapshotName = "T0046__custom_auth_config.json",
        applicationSetup = {
          install(Authentication) {
            oauth("auth-oauth-google") {
              urlProvider = { "http://localhost:8080/callback" }
              providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                  name = "google",
                  authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                  accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
                  requestMethod = HttpMethod.Post,
                  clientId = "DUMMY_VAL",
                  clientSecret = "DUMMY_VAL",
                  defaultScopes = listOf("https://www.googleapis.com/auth/userinfo.profile"),
                  extraTokenParameters = listOf("access_type" to "offline")
                )
              }
              client = HttpClient(CIO)
            }
          }
        },
        specOverrides = {
          this.copy(
            components = Components(
              securitySchemes = mutableMapOf(
                "auth-oauth-google" to OAuth(
                  flows = OAuth.Flows(
                    implicit = OAuth.Flows.Implicit(
                      authorizationUrl = "https://accounts.google.com/o/oauth2/auth",
                      scopes = mapOf(
                        "write:pets" to "modify pets in your account",
                        "read:pets" to "read your pets"
                      )
                    )
                  )
                )
              )
            )
          )
        }
      ) { customAuthConfig() }
    }
    it("Can provide multiple authentication strategies") {
      openApiTestAllSerializers(
        snapshotName = "T0047__multiple_auth_strategies.json",
        applicationSetup = {
          install(Authentication) {
            apiKey("api-key") {
              headerName = "X-API-KEY"
              validate {
                UserIdPrincipal("Placeholder")
              }
            }
            jwt("jwt") {
              realm = "Server"
            }
          }
        },
        specOverrides = {
          this.copy(
            components = Components(
              securitySchemes = mutableMapOf(
                "jwt" to BearerAuth("JWT"),
                "api-key" to ApiKeyAuth(ApiKeyAuth.ApiKeyLocation.HEADER, "X-API-KEY")
              )
            )
          )
        }
      ) { multipleAuthStrategies() }
    }
  }
  describe("Enrichment") {
    it("Can enrich a simple request") {
      openApiTestAllSerializers("T0055__enriched_simple_request.json") { enrichedSimpleRequest() }
    }
    it("Can enrich a simple response") {
      openApiTestAllSerializers("T0058__enriched_simple_response.json") { enrichedSimpleResponse() }
    }
    it("Can enrich a nested collection") {
      openApiTestAllSerializers("T0056__enriched_nested_collection.json") { enrichedNestedCollection() }
    }
    it("Can enrich a complex generic type") {
      openApiTestAllSerializers("T0057__enriched_complex_generic_type.json") { enrichedComplexGenericType() }
    }
  }
})
