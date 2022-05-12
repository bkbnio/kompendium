package io.bkbn.kompendium.core.fixtures

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import io.bkbn.kompendium.core.plugin.NotarizedApplication
import io.bkbn.kompendium.core.routes.redoc
import io.bkbn.kompendium.oas.OpenApiSpec
import io.bkbn.kompendium.oas.info.Contact
import io.bkbn.kompendium.oas.info.Info
import io.bkbn.kompendium.oas.info.License
import io.bkbn.kompendium.oas.serialization.KompendiumSerializersModule
import io.bkbn.kompendium.oas.server.Server
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.beBlank
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.gson.gson
import io.ktor.serialization.jackson.jackson
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.Routing
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import java.io.File
import java.net.URI

//import io.bkbn.kompendium.core.Kompendium
//import io.bkbn.kompendium.oas.serialization.KompendiumSerializersModule
//import io.kotest.assertions.json.shouldEqualJson
//import io.kotest.assertions.ktor.shouldHaveStatus
//import io.kotest.matchers.shouldBe
//import io.kotest.matchers.shouldNotBe
//import io.ktor.application.Application
//import io.ktor.application.install
//import io.ktor.features.ContentNegotiation
//import io.ktor.gson.gson
//import io.ktor.http.HttpMethod
//import io.ktor.http.HttpStatusCode
//import io.ktor.serialization.json
//import io.ktor.server.testing.TestApplicationEngine
//import io.ktor.server.testing.createTestEnvironment
//import io.ktor.server.testing.handleRequest
//import io.ktor.server.testing.withApplication
//import kotlinx.serialization.json.Json
//import java.io.File
//

object TestHelpers {
  private const val OPEN_API_ENDPOINT = "/openapi.json"
  const val DEFAULT_TEST_ENDPOINT = "/test"

  fun getFileSnapshot(fileName: String): String {
    val snapshotPath = "src/test/resources"
    val file = File("$snapshotPath/$fileName")
    return file.readText()
  }

  /**
   * Performs the baseline expected tests on an OpenAPI result.  Confirms that the endpoint
   * exists as expected, and that the content matches the expected blob found in the specified file
   * @param snapshotName The snapshot file to retrieve from the resources folder
   */
  suspend fun ApplicationTestBuilder.compareOpenAPISpec(snapshotName: String) {
    val response = client.get(OPEN_API_ENDPOINT)
    response shouldHaveStatus HttpStatusCode.OK
    response.bodyAsText() shouldNot beBlank()
    response.bodyAsText() shouldEqualJson getFileSnapshot(snapshotName)
  }

  /**
   * This will take a provided JSON snapshot file, retrieve it from the resource folder,
   * and build a test ktor server to compare the expected output with the output found in the default
   * OpenAPI json endpoint.  By default, this will run the same test with Gson, Kotlinx, and Jackson serializers
   * @param snapshotName The snapshot file to retrieve from the resources folder
   * @param moduleFunction Initializer for the application to allow tests to pass the required Ktor modules
   */
  fun openApiTestAllSerializers(snapshotName: String, routeUnderTest: Routing.() -> Unit) {
    openApiTest(snapshotName, SupportedSerializer.KOTLINX, routeUnderTest)
    openApiTest(snapshotName, SupportedSerializer.JACKSON, routeUnderTest)
    openApiTest(snapshotName, SupportedSerializer.GSON, routeUnderTest)
  }

  private fun openApiTest(
    snapshotName: String,
    serializer: SupportedSerializer,
    routeUnderTest: Routing.() -> Unit
  ) = testApplication {
    install(NotarizedApplication()) {
      spec = OpenApiSpec(
        info = Info(
          title = "Test API",
          version = "1.33.7",
          description = "An amazing, fully-ish \uD83D\uDE09 generated API spec",
          termsOfService = URI("https://example.com"),
          contact = Contact(
            name = "Homer Simpson",
            email = "chunkylover53@aol.com",
            url = URI("https://gph.is/1NPUDiM")
          ),
          license = License(
            name = "MIT",
            url = URI("https://github.com/bkbnio/kompendium/blob/main/LICENSE")
          )
        ),
        servers = mutableListOf(
          Server(
            url = URI("https://myawesomeapi.com"),
            description = "Production instance of my API"
          ),
          Server(
            url = URI("https://staging.myawesomeapi.com"),
            description = "Where the fun stuff happens"
          )
        )
      )
    }
    install(ContentNegotiation) {
      when (serializer) {
        SupportedSerializer.KOTLINX -> json(Json {
          encodeDefaults = true
          explicitNulls = false
          serializersModule = KompendiumSerializersModule.module
        })
        SupportedSerializer.GSON -> gson()
        SupportedSerializer.JACKSON -> jackson(ContentType.Application.Json) {
          enable(SerializationFeature.INDENT_OUTPUT)
          setSerializationInclusion(JsonInclude.Include.NON_NULL)
        }
      }
    }
    routing {
      redoc()
      routeUnderTest()
    }
    compareOpenAPISpec(snapshotName)
  }
}

//object TestHelpers {

//  fun openApiTestAllSerializers(snapshotName: String, moduleFunction: Application.() -> Unit) {
//    openApiTestAllSerializers(snapshotName, moduleFunction) {}
//  }
//
//  fun openApiTestAllSerializers(
//    snapshotName: String, moduleFunction: Application.() -> Unit,
//    kompendiumConfigurer: Kompendium.Configuration.() -> Unit
//  ) {
//    openApiTest(snapshotName, SupportedSerializer.KOTLINX, moduleFunction, kompendiumConfigurer)
//    openApiTest(snapshotName, SupportedSerializer.JACKSON, moduleFunction, kompendiumConfigurer)
//    openApiTest(snapshotName, SupportedSerializer.GSON, moduleFunction, kompendiumConfigurer)
//  }
//
//  private fun openApiTest(
//    snapshotName: String,
//    serializer: SupportedSerializer,
//    moduleFunction: Application.() -> Unit,
//    kompendiumConfigurer: Kompendium.Configuration.() -> Unit
//  ) {
//    withApplication(createTestEnvironment()) {
//      moduleFunction(application.apply {
//        kompendium(kompendiumConfigurer)
//        docs()
//        when (serializer) {
//          SupportedSerializer.KOTLINX -> {
//            install(ContentNegotiation) {
//              json(Json {
//                encodeDefaults = true
//                explicitNulls = false
//                serializersModule = KompendiumSerializersModule.module
//              })
//            }
//          }
//          SupportedSerializer.GSON -> {
//            install(ContentNegotiation) {
//              gson()
//            }
//          }
//          SupportedSerializer.JACKSON -> {
//            jacksonConfigModule()
//          }
//        }
//      })
//      compareOpenAPISpec(snapshotName)
//    }
//  }
//
//  /**
//   * This allows us to easily confirm that an API endpoints functionality is not impacted by notarization.
//   * @param httpMethod The HTTP operation that we wish to perform
//   * @param endpoint The relative endpoint we wish to hit
//   * @param expectedResponse The expected response content of the operation
//   * @param expectedStatusCode The expected status code of the operation
//   */
//  fun apiFunctionalityTest(
//    expectedResponse: String?,
//    endpoint: String = DEFAULT_TEST_ENDPOINT,
//    httpMethod: HttpMethod = HttpMethod.Get,
//    expectedStatusCode: HttpStatusCode = HttpStatusCode.OK,
//    moduleFunction: Application.() -> Unit
//  ) {
//    withApplication(createTestEnvironment()) {
//      moduleFunction(application.apply {
//        kompendium()
//        docs()
//        jacksonConfigModule()
//      })
//      when (expectedResponse) {
//        null -> testWithNoResponse(httpMethod, endpoint, expectedStatusCode)
//        else -> testWithResponse(httpMethod, expectedResponse, endpoint, expectedStatusCode)
//      }
//    }
//  }
//
//  private fun TestApplicationEngine.testWithResponse(
//    httpMethod: HttpMethod,
//    expectedResponse: String,
//    endpoint: String,
//    expectedStatusCode: HttpStatusCode,
//  ) {
//    handleRequest(httpMethod, endpoint).apply {
//      // assert
//      response shouldHaveStatus expectedStatusCode
//      response.content shouldNotBe null
//      response.content shouldBe expectedResponse
//    }
//  }
//
//  private fun TestApplicationEngine.testWithNoResponse(
//    httpMethod: HttpMethod,
//    endpoint: String,
//    expectedStatusCode: HttpStatusCode,
//  ) {
//    handleRequest(httpMethod, endpoint).apply {
//      // assert
//      response shouldHaveStatus expectedStatusCode
//      response.content shouldBe null
//    }
//  }
//}
