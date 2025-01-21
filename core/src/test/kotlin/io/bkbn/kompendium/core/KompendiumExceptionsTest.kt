package io.bkbn.kompendium.core

import io.bkbn.kompendium.core.fixtures.TestHelpers
import io.bkbn.kompendium.core.util.genericException
import io.bkbn.kompendium.core.util.multipleExceptions
import io.bkbn.kompendium.core.util.polymorphicException
import io.bkbn.kompendium.core.util.singleException
import io.kotest.core.spec.style.DescribeSpec

class KompendiumExceptionsTest : DescribeSpec({
  describe("Exceptions") {
    it("Can add an exception status code to a response") {
      TestHelpers.openApiTestAllSerializers("T0016__notarized_get_with_exception_response.json") { singleException() }
    }
    it("Can support multiple response codes") {
      TestHelpers.openApiTestAllSerializers("T0017__notarized_get_with_multiple_exception_responses.json") {
        multipleExceptions()
      }
    }
    it("Can add a polymorphic exception response") {
      TestHelpers.openApiTestAllSerializers("T0018__polymorphic_error_status_codes.json") { polymorphicException() }
    }
    it("Can add a generic exception response") {
      TestHelpers.openApiTestAllSerializers("T0019__generic_exception.json") { genericException() }
    }
  }
})
