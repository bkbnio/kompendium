package io.bkbn.kompendium.core

import io.bkbn.kompendium.oas.info.Contact
import io.bkbn.kompendium.oas.info.Info
import io.bkbn.kompendium.oas.info.License
import io.bkbn.kompendium.oas.server.Server
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.createTestEnvironment
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withApplication
import java.io.File
import java.net.URI

object TestHelpers {

  const val OPEN_API_ENDPOINT = "/openapi.json"
  const val DEFAULT_TEST_ENDPOINT = "/test"

  fun getFileSnapshot(fileName: String): String {
    val snapshotPath = "src/test/resources"
    val file = File("$snapshotPath/$fileName")
    return file.readText()
  }

  fun oas() = Kompendium.openApiSpec.copy(
    info = Info(
      title = "Test API",
      version = "1.33.7",
      description = "An amazing, fully-ish 😉 generated API spec",
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

  /**
   * Performs the baseline expected tests on an OpenAPI result.  Confirms that the endpoint
   * exists as expected, and that the content matches the expected blob found in the specified file
   * @param snapshotName The snapshot file to retrieve from the resources folder
   */
  fun TestApplicationEngine.compareOpenAPISpec(snapshotName: String) {
    // act
    handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
      // assert
      response shouldHaveStatus HttpStatusCode.OK
      response.content shouldNotBe null
      response.content!! shouldEqualJson getFileSnapshot(snapshotName)
    }
  }

  /**
   * This will take a provided JSON snapshot file, retrieve it from the resource folder,
   * and build a test ktor server to compare the expected output with the output found in the default
   * OpenAPI json endpoint
   * @param snapshotName The snapshot file to retrieve from the resources folder
   * @param moduleFunction Initializer for the application to allow tests to pass the required Ktor modules
   */
  fun openApiTest(snapshotName: String, moduleFunction: Application.() -> Unit) {
    withApplication(createTestEnvironment()) {
      moduleFunction(application.apply {
        docs()
        jacksonConfigModule()
      })
      compareOpenAPISpec(snapshotName)
    }
  }

  /**
   * This allows us to easily confirm that an API endpoints functionality is not impacted by notarization.
   * @param httpMethod The HTTP operation that we wish to perform
   * @param endpoint The relative endpoint we wish to hit
   * @param expectedResponse The expected response content of the operation
   * @param expectedStatusCode The expected status code of the operation
   */
  fun apiFunctionalityTest(
    expectedResponse: String?,
    endpoint: String = DEFAULT_TEST_ENDPOINT,
    httpMethod: HttpMethod = HttpMethod.Get,
    expectedStatusCode: HttpStatusCode = HttpStatusCode.OK,
    moduleFunction: Application.() -> Unit
  ) {
    withApplication(createTestEnvironment()) {
      moduleFunction(application.apply {
        docs()
        jacksonConfigModule()
      })
      when (expectedResponse) {
        null -> testWithNoResponse(httpMethod, endpoint, expectedStatusCode)
        else -> testWithResponse(httpMethod, expectedResponse, endpoint, expectedStatusCode)
      }
    }
  }

  private fun TestApplicationEngine.testWithResponse(
    httpMethod: HttpMethod,
    expectedResponse: String,
    endpoint: String,
    expectedStatusCode: HttpStatusCode,
  ) {
    handleRequest(httpMethod, endpoint).apply {
      // assert
      response shouldHaveStatus expectedStatusCode
      response.content shouldNotBe null
      response.content shouldBe expectedResponse
    }
  }

  private fun TestApplicationEngine.testWithNoResponse(
    httpMethod: HttpMethod,
    endpoint: String,
    expectedStatusCode: HttpStatusCode,
  ) {
    handleRequest(httpMethod, endpoint).apply {
      // assert
      response shouldHaveStatus expectedStatusCode
      response.content shouldBe null
    }
  }
}
