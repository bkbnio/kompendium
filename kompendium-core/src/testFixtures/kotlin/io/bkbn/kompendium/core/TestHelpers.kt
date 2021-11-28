package io.bkbn.kompendium.core

import io.bkbn.kompendium.oas.info.Contact
import io.bkbn.kompendium.oas.info.Info
import io.bkbn.kompendium.oas.info.License
import io.bkbn.kompendium.oas.server.Server
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.matchers.shouldNotBe
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import java.io.File
import java.net.URI

object TestHelpers {

  const val OPEN_API_ENDPOINT = "/openapi.json"

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

  fun TestApplicationEngine.compareOpenAPISpec(snapshotName: String) {
    // act
    handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
      // assert
      response shouldHaveStatus HttpStatusCode.OK
      response.content shouldNotBe null
      response.content!! shouldEqualJson getFileSnapshot(snapshotName)
    }
  }
}
