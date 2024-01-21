package io.bkbn.kompendium.core

import io.bkbn.kompendium.core.fixtures.TestHelpers
import io.bkbn.kompendium.core.util.dateTimeString
import io.bkbn.kompendium.core.util.samePathSameMethod
import io.bkbn.kompendium.json.schema.exception.UnknownSchemaException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.should
import io.kotest.matchers.string.startWith
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic

class KompendiumErrorHandlingTest : DescribeSpec({
  describe("Error Handling") {
    it("Throws a clear exception when an unidentified type is encountered") {
      val exception = shouldThrow<UnknownSchemaException> {
        TestHelpers.openApiTestAllSerializers(
          ""
        ) { dateTimeString() }
      }
      exception.message should startWith("An unknown type was encountered: class java.time.Instant")
    }
    it("Throws an exception when same method for same path has been previously registered") {
      val exception = shouldThrow<IllegalArgumentException> {
        TestHelpers.openApiTestAllSerializers(
          snapshotName = "",
          applicationSetup = {
            install(Authentication) {
              basic("basic") {
                realm = "Ktor Server"
                validate { UserIdPrincipal("Placeholder") }
              }
            }
          },
        ) {
          samePathSameMethod()
        }
      }
      exception.message should startWith("A route has already been registered for path: /test/{a} and method: GET")
    }
  }
})
