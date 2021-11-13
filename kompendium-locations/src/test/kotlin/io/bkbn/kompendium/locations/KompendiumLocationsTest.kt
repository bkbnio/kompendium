package io.bkbn.kompendium.locations

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import io.bkbn.kompendium.core.Kompendium
import io.bkbn.kompendium.core.annotations.KompendiumParam
import io.bkbn.kompendium.core.annotations.ParamType
import io.bkbn.kompendium.core.metadata.MethodInfo
import io.bkbn.kompendium.core.metadata.RequestInfo
import io.bkbn.kompendium.core.metadata.ResponseInfo
import io.bkbn.kompendium.core.routes.openApi
import io.bkbn.kompendium.core.routes.redoc
import io.bkbn.kompendium.locations.NotarizedLocation.notarizedDelete
import io.bkbn.kompendium.locations.NotarizedLocation.notarizedGet
import io.bkbn.kompendium.locations.NotarizedLocation.notarizedPost
import io.bkbn.kompendium.locations.NotarizedLocation.notarizedPut
import io.bkbn.kompendium.locations.util.TestData
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.locations.Location
import io.ktor.locations.Locations
import io.ktor.response.respondText
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.junit.Test
import kotlin.test.AfterTest
import kotlin.test.assertEquals

class KompendiumLocationsTest {

  @AfterTest
  fun `reset Kompendium`() {
    Kompendium.resetSchema()
  }

  @Test
  fun `Notarized Get with simple location`() {
    withTestApplication({
      configModule()
      docs()
      notarizedGetSimpleLocation()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = TestData.getFileSnapshot("notarized_get_simple_location.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Notarized Get with nested location`() {
    withTestApplication({
      configModule()
      docs()
      notarizedGetNestedLocation()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = TestData.getFileSnapshot("notarized_get_nested_location.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Notarized Post with simple location`() {
    withTestApplication({
      configModule()
      docs()
      notarizedPostSimpleLocation()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = TestData.getFileSnapshot("notarized_post_simple_location.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Notarized Post with nested location`() {
    withTestApplication({
      configModule()
      docs()
      notarizedPostNestedLocation()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = TestData.getFileSnapshot("notarized_post_nested_location.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Notarized Put with simple location`() {
    withTestApplication({
      configModule()
      docs()
      notarizedPutSimpleLocation()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = TestData.getFileSnapshot("notarized_put_simple_location.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Notarized Put with nested location`() {
    withTestApplication({
      configModule()
      docs()
      notarizedPutNestedLocation()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = TestData.getFileSnapshot("notarized_put_nested_location.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Notarized Delete with simple location`() {
    withTestApplication({
      configModule()
      docs()
      notarizedDeleteSimpleLocation()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = TestData.getFileSnapshot("notarized_delete_simple_location.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  @Test
  fun `Notarized Delete with nested location`() {
    withTestApplication({
      configModule()
      docs()
      notarizedDeleteNestedLocation()
    }) {
      // do
      val json = handleRequest(HttpMethod.Get, "/openapi.json").response.content

      // expect
      val expected = TestData.getFileSnapshot("notarized_delete_nested_location.json").trim()
      assertEquals(expected, json, "The received json spec should match the expected content")
    }
  }

  private fun Application.configModule() {
    install(ContentNegotiation) {
      jackson(ContentType.Application.Json) {
        enable(SerializationFeature.INDENT_OUTPUT)
        setSerializationInclusion(JsonInclude.Include.NON_NULL)
      }
    }
    install(Locations)
  }

  private val oas = Kompendium.openApiSpec.copy()

  private fun Application.docs() {
    routing {
      openApi(oas)
      redoc(oas)
    }
  }

  private fun Application.notarizedGetSimpleLocation() {
    routing {
      route("/test") {
        notarizedGet(TestResponseInfo.testGetSimpleLocation) {
          call.respondText { "hey dude ‼️ congratz on the get request" }
        }
      }
    }
  }

  private fun Application.notarizedGetNestedLocation() {
    routing {
      route("/test") {
        notarizedGet(TestResponseInfo.testGetNestedLocation) {
          call.respondText { "hey dude ‼️ congratz on the get request" }
        }
      }
    }
  }

  private fun Application.notarizedPostSimpleLocation() {
    routing {
      route("/test") {
        notarizedPost(TestResponseInfo.testPostSimpleLocation) {
          call.respondText { "hey dude ‼️ congratz on the get request" }
        }
      }
    }
  }

  private fun Application.notarizedPostNestedLocation() {
    routing {
      route("/test") {
        notarizedPost(TestResponseInfo.testPostNestedLocation) {
          call.respondText { "hey dude ‼️ congratz on the get request" }
        }
      }
    }
  }

  private fun Application.notarizedPutSimpleLocation() {
    routing {
      route("/test") {
        notarizedPut(TestResponseInfo.testPutSimpleLocation) {
          call.respondText { "hey dude ‼️ congratz on the get request" }
        }
      }
    }
  }

  private fun Application.notarizedPutNestedLocation() {
    routing {
      route("/test") {
        notarizedPut(TestResponseInfo.testPutNestedLocation) {
          call.respondText { "hey dude ‼️ congratz on the get request" }
        }
      }
    }
  }

  private fun Application.notarizedDeleteSimpleLocation() {
    routing {
      route("/test") {
        notarizedDelete(TestResponseInfo.testDeleteSimpleLocation) {
          call.respondText { "hey dude ‼️ congratz on the get request" }
        }
      }
    }
  }

  private fun Application.notarizedDeleteNestedLocation() {
    routing {
      route("/test") {
        notarizedDelete(TestResponseInfo.testDeleteNestedLocation) {
          call.respondText { "hey dude ‼️ congratz on the get request" }
        }
      }
    }
  }

  object TestResponseInfo {
    val testGetSimpleLocation = MethodInfo.GetInfo<SimpleLoc, SimpleResponse>(
      summary = "Location Test",
      description = "A cool test",
      responseInfo = ResponseInfo(
        status = HttpStatusCode.OK,
        description = "A successful endeavor"
      )
    )
    val testPostSimpleLocation = MethodInfo.PostInfo<SimpleLoc, SimpleRequest, SimpleResponse>(
      summary = "Location Test",
      description = "A cool test",
      requestInfo = RequestInfo(
        description = "Cool stuff"
      ),
      responseInfo = ResponseInfo(
        status = HttpStatusCode.OK,
        description = "A successful endeavor"
      )
    )
    val testPutSimpleLocation = MethodInfo.PutInfo<SimpleLoc, SimpleRequest, SimpleResponse>(
      summary = "Location Test",
      description = "A cool test",
      requestInfo = RequestInfo(
        description = "Cool stuff"
      ),
      responseInfo = ResponseInfo(
        status = HttpStatusCode.OK,
        description = "A successful endeavor"
      )
    )
    val testDeleteSimpleLocation = MethodInfo.DeleteInfo<SimpleLoc, SimpleResponse>(
      summary = "Location Test",
      description = "A cool test",
      responseInfo = ResponseInfo(
        status = HttpStatusCode.OK,
        description = "A successful endeavor"
      )
    )
    val testGetNestedLocation = MethodInfo.GetInfo<SimpleLoc.NestedLoc, SimpleResponse>(
      summary = "Location Test",
      description = "A cool test",
      responseInfo = ResponseInfo(
        status = HttpStatusCode.OK,
        description = "A successful endeavor"
      )
    )
    val testPostNestedLocation = MethodInfo.PostInfo<SimpleLoc.NestedLoc, SimpleRequest, SimpleResponse>(
      summary = "Location Test",
      description = "A cool test",
      requestInfo = RequestInfo(
        description = "Cool stuff"
      ),
      responseInfo = ResponseInfo(
        status = HttpStatusCode.OK,
        description = "A successful endeavor"
      )
    )
    val testPutNestedLocation = MethodInfo.PutInfo<SimpleLoc.NestedLoc, SimpleRequest, SimpleResponse>(
      summary = "Location Test",
      description = "A cool test",
      requestInfo = RequestInfo(
        description = "Cool stuff"
      ),
      responseInfo = ResponseInfo(
        status = HttpStatusCode.OK,
        description = "A successful endeavor"
      )
    )
    val testDeleteNestedLocation = MethodInfo.DeleteInfo<SimpleLoc.NestedLoc, SimpleResponse>(
      summary = "Location Test",
      description = "A cool test",
      responseInfo = ResponseInfo(
        status = HttpStatusCode.OK,
        description = "A successful endeavor"
      )
    )
  }
}

@Location("/test/{name}")
data class SimpleLoc(@KompendiumParam(ParamType.PATH) val name: String) {
  @Location("/nesty")
  data class NestedLoc(@KompendiumParam(ParamType.QUERY) val isCool: Boolean, val parent: SimpleLoc)
}

data class SimpleResponse(val result: Boolean)
data class SimpleRequest(val input: String)
