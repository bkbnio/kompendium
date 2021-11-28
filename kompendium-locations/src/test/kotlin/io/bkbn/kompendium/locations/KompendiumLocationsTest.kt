package io.bkbn.kompendium.locations

import io.bkbn.kompendium.core.Kompendium
import io.bkbn.kompendium.core.TestHelpers.compareOpenAPISpec
import io.bkbn.kompendium.core.docs
import io.bkbn.kompendium.core.jacksonConfigModule
import io.bkbn.kompendium.locations.util.locationsConfig
import io.bkbn.kompendium.locations.util.notarizedDeleteNestedLocation
import io.bkbn.kompendium.locations.util.notarizedDeleteSimpleLocation
import io.bkbn.kompendium.locations.util.notarizedGetNestedLocation
import io.bkbn.kompendium.locations.util.notarizedGetSimpleLocation
import io.bkbn.kompendium.locations.util.notarizedPostNestedLocation
import io.bkbn.kompendium.locations.util.notarizedPostSimpleLocation
import io.bkbn.kompendium.locations.util.notarizedPutNestedLocation
import io.bkbn.kompendium.locations.util.notarizedPutSimpleLocation
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.server.testing.withTestApplication

class KompendiumLocationsTest : DescribeSpec({
  afterEach { Kompendium.resetSchema() }
  describe("Locations") {
    it("Can notarize a get request with a simple location") {
      // arrange
      withTestApplication({
        locationsConfig()
        jacksonConfigModule()
        docs()
        notarizedGetSimpleLocation()
      }) {
        // act
        compareOpenAPISpec("notarized_get_simple_location.json")
      }
    }
    it("Can notarize a get request with a nested location") {
      // arrange
      withTestApplication({
        locationsConfig()
        jacksonConfigModule()
        docs()
        notarizedGetNestedLocation()
      }) {
        // act
        compareOpenAPISpec("notarized_get_nested_location.json")
      }
    }
    it("Can notarize a post with a simple location") {
      // arrange
      withTestApplication({
        locationsConfig()
        jacksonConfigModule()
        docs()
        notarizedPostSimpleLocation()
      }) {
        // act
        compareOpenAPISpec("notarized_post_simple_location.json")
      }
    }
    it("Can notarize a post with a nested location") {
      // arrange
      withTestApplication({
        locationsConfig()
        jacksonConfigModule()
        docs()
        notarizedPostNestedLocation()
      }) {
        // act
        compareOpenAPISpec("notarized_post_nested_location.json")
      }
    }
    it("Can notarize a put with a simple location") {
      // arrange
      withTestApplication({
        locationsConfig()
        jacksonConfigModule()
        docs()
        notarizedPutSimpleLocation()
      }) {
        // act
        compareOpenAPISpec("notarized_put_simple_location.json")
      }
    }
    it("Can notarize a put with a nested location") {
      // arrange
      withTestApplication({
        locationsConfig()
        jacksonConfigModule()
        docs()
        notarizedPutNestedLocation()
      }) {
        // act
        compareOpenAPISpec("notarized_put_nested_location.json")
      }
    }
    it("Can notarize a delete with a simple location") {
      // arrange
      withTestApplication({
        locationsConfig()
        jacksonConfigModule()
        docs()
        notarizedDeleteSimpleLocation()
      }) {
        // act
        compareOpenAPISpec("notarized_delete_simple_location.json")
      }
    }
    it("Can notarize a delete with a nested location") {
      // arrange
      withTestApplication({
        locationsConfig()
        jacksonConfigModule()
        docs()
        notarizedDeleteNestedLocation()
      }) {
        // act
        compareOpenAPISpec("notarized_delete_nested_location.json")
      }
    }
  }
})
