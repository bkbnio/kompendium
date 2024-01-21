package io.bkbn.kompendium.core

import io.bkbn.kompendium.core.fixtures.TestHelpers
import io.bkbn.kompendium.core.util.exampleParams
import io.bkbn.kompendium.core.util.exampleSummaryAndDescription
import io.bkbn.kompendium.core.util.optionalReqExample
import io.bkbn.kompendium.core.util.reqRespExamples
import io.kotest.core.spec.style.DescribeSpec

class KompendiumExamplesTest : DescribeSpec({
  describe("Examples") {
    it("Can generate example response and request bodies") {
      TestHelpers.openApiTestAllSerializers("T0020__example_req_and_resp.json") { reqRespExamples() }
    }
    it("Can describe example parameters") {
      TestHelpers.openApiTestAllSerializers("T0021__example_parameters.json") { exampleParams() }
    }
    it("Can generate example optional request body") {
      TestHelpers.openApiTestAllSerializers("T0069__example_optional_req.json") { optionalReqExample() }
    }
    it("Can generate example summary and description") {
      TestHelpers.openApiTestAllSerializers(
        "T0075__example_summary_and_description.json"
      ) { exampleSummaryAndDescription() }
    }
  }
})
