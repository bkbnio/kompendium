package io.bkbn.kompendium.core

import io.bkbn.kompendium.core.fixtures.TestHelpers.openApiTestAllSerializers
import io.bkbn.kompendium.core.util.enrichedComplexGenericType
import io.bkbn.kompendium.core.util.enrichedGenericResponse
import io.bkbn.kompendium.core.util.enrichedMap
import io.bkbn.kompendium.core.util.enrichedNestedCollection
import io.bkbn.kompendium.core.util.enrichedSimpleRequest
import io.bkbn.kompendium.core.util.enrichedSimpleResponse
import io.bkbn.kompendium.core.util.enrichedTopLevelCollection
import io.kotest.core.spec.style.DescribeSpec

class KompendiumEnrichmentTest : DescribeSpec({
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
      openApiTestAllSerializers(
        "T0057__enriched_complex_generic_type.json"
      ) { enrichedComplexGenericType() }
    }
    it("Can enrich a generic object") {
      openApiTestAllSerializers("T0067__enriched_generic_object.json") { enrichedGenericResponse() }
    }
    it("Can enrich a top level list type") {
      openApiTestAllSerializers("T0077__enriched_top_level_list.json") { enrichedTopLevelCollection() }
    }
    it("can enrich a map type") {
      openApiTestAllSerializers("T0078__enriched_top_level_map.json") { enrichedMap() }
    }
  }
})
