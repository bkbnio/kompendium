package io.bkbn.kompendium.core

import io.bkbn.kompendium.core.fixtures.TestHelpers
import io.bkbn.kompendium.core.util.defaultParameter
import io.kotest.core.spec.style.DescribeSpec

class KompendiumDefaultsTest : DescribeSpec({
  describe("Defaults") {
    it("Can generate a default parameter value") {
      TestHelpers.openApiTestAllSerializers("T0022__query_with_default_parameter.json") { defaultParameter() }
    }
  }
})
