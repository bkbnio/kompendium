package io.bkbn.kompendium.core

import io.bkbn.kompendium.core.fixtures.TestHelpers
import io.bkbn.kompendium.core.util.arrayConstraints
import io.bkbn.kompendium.core.util.doubleConstraints
import io.bkbn.kompendium.core.util.intConstraints
import io.bkbn.kompendium.core.util.stringConstraints
import io.bkbn.kompendium.core.util.stringContentEncodingConstraints
import io.bkbn.kompendium.core.util.stringPatternConstraints
import io.kotest.core.spec.style.DescribeSpec

class KompendiumConstraintsTest : DescribeSpec({
  describe("Constraints") {
    it("Can apply constraints to an int field") {
      TestHelpers.openApiTestAllSerializers("T0059__int_constraints.json") { intConstraints() }
    }
    it("Can apply constraints to a double field") {
      TestHelpers.openApiTestAllSerializers("T0060__double_constraints.json") { doubleConstraints() }
    }
    it("Can apply a min and max length to a string field") {
      TestHelpers.openApiTestAllSerializers("T0061__string_min_max_constraints.json") { stringConstraints() }
    }
    it("Can apply a pattern to a string field") {
      TestHelpers.openApiTestAllSerializers("T0062__string_pattern_constraints.json") { stringPatternConstraints() }
    }
    it("Can apply a content encoding and media type to a string field") {
      TestHelpers.openApiTestAllSerializers("T0063__string_content_encoding_constraints.json") {
        stringContentEncodingConstraints()
      }
    }
    it("Can apply constraints to an array field") {
      TestHelpers.openApiTestAllSerializers("T0064__array_constraints.json") { arrayConstraints() }
    }
  }
})
