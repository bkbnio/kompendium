package io.bkbn.kompendium.locations

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import io.bkbn.kompendium.core.Kompendium
import io.bkbn.kompendium.annotations.KompendiumParam
import io.bkbn.kompendium.annotations.ParamType
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
import io.bkbn.kompendium.locations.util.TestData.OPEN_API_ENDPOINT
import io.bkbn.kompendium.locations.util.configModule
import io.bkbn.kompendium.locations.util.docs
import io.bkbn.kompendium.locations.util.notarizedDeleteNestedLocation
import io.bkbn.kompendium.locations.util.notarizedDeleteSimpleLocation
import io.bkbn.kompendium.locations.util.notarizedGetNestedLocation
import io.bkbn.kompendium.locations.util.notarizedGetSimpleLocation
import io.bkbn.kompendium.locations.util.notarizedPostNestedLocation
import io.bkbn.kompendium.locations.util.notarizedPostSimpleLocation
import io.bkbn.kompendium.locations.util.notarizedPutNestedLocation
import io.bkbn.kompendium.locations.util.notarizedPutSimpleLocation
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldNotBe
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

class KompendiumLocationsTest : DescribeSpec({
  afterEach { Kompendium.resetSchema() }
  describe("Locations") {
    it("Can notarize a get request with a simple location") {
      withTestApplication({
        configModule()
        docs()
        notarizedGetSimpleLocation()
      }) {
        // arrange
        val expected = TestData.getFileSnapshot("notarized_get_simple_location.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content!! shouldEqualJson expected
        }
      }
    }
    it("Can notarize a get request with a nested location") {
      withTestApplication({
        configModule()
        docs()
        notarizedGetNestedLocation()
      }) {
        // arrange
        val expected = TestData.getFileSnapshot("notarized_get_nested_location.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content!! shouldEqualJson expected
        }
      }
    }
    it("Can notarize a post with a simple location") {
      withTestApplication({
        configModule()
        docs()
        notarizedPostSimpleLocation()
      }) {
        // arrange
        val expected = TestData.getFileSnapshot("notarized_post_simple_location.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content!! shouldEqualJson expected
        }
      }
    }
    it("Can notarize a post with a nested location") {
      withTestApplication({
        configModule()
        docs()
        notarizedPostNestedLocation()
      }) {
        // arrange
        val expected = TestData.getFileSnapshot("notarized_post_nested_location.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content!! shouldEqualJson expected
        }
      }
    }
    it("Can notarize a put with a simple location") {
      withTestApplication({
        configModule()
        docs()
        notarizedPutSimpleLocation()
      }) {
        // arrange
        val expected = TestData.getFileSnapshot("notarized_put_simple_location.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content!! shouldEqualJson expected
        }
      }
    }
    it("Can notarize a put with a nested location") {
      withTestApplication({
        configModule()
        docs()
        notarizedPutNestedLocation()
      }) {
        // arrange
        val expected = TestData.getFileSnapshot("notarized_put_nested_location.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content!! shouldEqualJson expected
        }
      }
    }
    it("Can notarize a delete with a simple location") {
      withTestApplication({
        configModule()
        docs()
        notarizedDeleteSimpleLocation()
      }) {
        // arrange
        val expected = TestData.getFileSnapshot("notarized_delete_simple_location.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content!! shouldEqualJson expected
        }
      }
    }
    it("Can notarize a delete with a nested location") {
      withTestApplication({
        configModule()
        docs()
        notarizedDeleteNestedLocation()
      }) {
        // arrange
        val expected = TestData.getFileSnapshot("notarized_delete_nested_location.json").trim()

        // act
        handleRequest(HttpMethod.Get, OPEN_API_ENDPOINT).apply {
          // assert
          response shouldHaveStatus HttpStatusCode.OK
          response.content shouldNotBe null
          response.content!! shouldEqualJson expected
        }
      }
    }
  }
})
