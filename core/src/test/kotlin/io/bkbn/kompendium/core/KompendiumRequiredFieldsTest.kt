package io.bkbn.kompendium.core

import io.bkbn.kompendium.core.fixtures.TestHelpers
import io.bkbn.kompendium.core.util.defaultField
import io.bkbn.kompendium.core.util.nonRequiredParam
import io.bkbn.kompendium.core.util.nullableField
import io.bkbn.kompendium.core.util.requiredParams
import io.kotest.core.spec.style.DescribeSpec

class KompendiumRequiredFieldsTest : DescribeSpec({
  describe("Required Fields") {
    it("Marks a parameter as required if there is no default and it is not marked nullable") {
      TestHelpers.openApiTestAllSerializers("T0023__required_param.json") { requiredParams() }
    }
    it("Can mark a parameter as not required") {
      TestHelpers.openApiTestAllSerializers("T0024__non_required_param.json") { nonRequiredParam() }
    }
    it("Does not mark a field as required if a default value is provided") {
      TestHelpers.openApiTestAllSerializers("T0025__default_field.json") { defaultField() }
    }
    it("Does not mark a nullable field as required") {
      TestHelpers.openApiTestAllSerializers("T0026__nullable_field.json") { nullableField() }
    }
  }
})
