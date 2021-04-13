package org.leafygreens.kompendium

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpMethod
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import java.net.URI
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.leafygreens.kompendium.Kompendium.notarizedGet
import org.leafygreens.kompendium.KompendiumTest.Companion.KompendiumToC.testSingleGetInfo
import org.leafygreens.kompendium.models.meta.MethodInfo
import org.leafygreens.kompendium.models.oas.OpenApiSpecInfo
import org.leafygreens.kompendium.models.oas.OpenApiSpecInfoContact
import org.leafygreens.kompendium.models.oas.OpenApiSpecInfoLicense
import org.leafygreens.kompendium.models.oas.OpenApiSpecServer
import org.leafygreens.kompendium.util.TestData
import org.leafygreens.kompendium.util.TestRequest
import org.leafygreens.kompendium.util.TestResponse

internal class KompendiumTest {

  @AfterTest
  fun `reset kompendium`() {
    Kompendium.resetSchema()
  }

  @Test
  fun `Kompendium can be instantiated with no details`() {
    assertEquals(Kompendium.openApiSpec.openapi, "3.0.3", "Kompendium has a default spec version of 3.0.3")
  }

  @Test
  fun `Notarized Get records all expected information`() {
    withTestApplication({
      configModule()
      openApiModule()
      notarizedGetModule()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = TestData.getFileSnapshot("notarized_get.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  private companion object {
    object KompendiumToC {
      val testIdGetInfo = MethodInfo("Get Test", "Test for getting", tags = setOf("test", "example", "get"))
      val testSingleGetInfo = MethodInfo("Another get test", "testing more")
      val testSinglePostInfo = MethodInfo("Test post endpoint", "Post your tests here!")
      val testSinglePutInfo = MethodInfo("Test put endpoint", "Put your tests here!")
    }
  }

  private fun Application.configModule() {
    install(ContentNegotiation) {
      jackson {
        enable(SerializationFeature.INDENT_OUTPUT)
        setSerializationInclusion(JsonInclude.Include.NON_NULL)
      }
    }
  }

  private fun Application.notarizedGetModule() {
    routing {
      route("/test") {
        notarizedGet<TestRequest, TestResponse>(testSingleGetInfo) {
          call.respondText("get single")
        }
      }
    }
  }

  private fun Application.openApiModule() {
    routing {
      route("/openapi.json") {
        get {
          call.respond(
            Kompendium.openApiSpec.copy(
              info = OpenApiSpecInfo(
                title = "Test API",
                version = "1.33.7",
                description = "An amazing, fully-ish 😉 generated API spec",
                termsOfService = URI("https://example.com"),
                contact = OpenApiSpecInfoContact(
                  name = "Homer Simpson",
                  email = "chunkylover53@aol.com",
                  url = URI("https://gph.is/1NPUDiM")
                ),
                license = OpenApiSpecInfoLicense(
                  name = "MIT",
                  url = URI("https://github.com/lg-backbone/kompendium/blob/main/LICENSE")
                )
              ),
              servers = mutableListOf(
                OpenApiSpecServer(
                  url = URI("https://myawesomeapi.com"),
                  description = "Production instance of my API"
                ),
                OpenApiSpecServer(
                  url = URI("https://staging.myawesomeapi.com"),
                  description = "Where the fun stuff happens"
                )
              )
            )
          )
        }
      }
    }
  }

}
