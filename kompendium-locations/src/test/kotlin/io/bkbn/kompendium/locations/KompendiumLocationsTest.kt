package io.bkbn.kompendium.locations

import io.bkbn.kompendium.core.Kompendium
import io.bkbn.kompendium.core.TestHelpers.compareOpenAPISpec
import io.bkbn.kompendium.core.TestHelpers.openApiTest
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
      // act
      openApiTest("notarized_get_simple_location.json") {
        locationsConfig()
        notarizedGetSimpleLocation()
      }
    }
    it("Can notarize a get request with a nested location") {
      // act
      openApiTest("notarized_get_nested_location.json") {
        locationsConfig()
        notarizedGetNestedLocation()
      }
    }
    it("Can notarize a post with a simple location") {
      // act
      openApiTest("notarized_post_simple_location.json") {
        locationsConfig()
        notarizedPostSimpleLocation()
      }
    }
    it("Can notarize a post with a nested location") {
      // act
      openApiTest("notarized_post_nested_location.json") {
        locationsConfig()
        notarizedPostNestedLocation()
      }
    }
    it("Can notarize a put with a simple location") {
      // act
      openApiTest("notarized_put_simple_location.json") {
        locationsConfig()
        notarizedPutSimpleLocation()
      }
    }
    it("Can notarize a put with a nested location") {
      // act
      openApiTest("notarized_put_nested_location.json") {
        locationsConfig()
        notarizedPutNestedLocation()
      }
    }
    it("Can notarize a delete with a simple location") {
      // act
      openApiTest("notarized_delete_simple_location.json") {
        locationsConfig()
        notarizedDeleteSimpleLocation()
      }
    }
    it("Can notarize a delete with a nested location") {
      // act
      openApiTest("notarized_delete_nested_location.json") {
        locationsConfig()
        notarizedDeleteNestedLocation()
      }
    }
  }
})
