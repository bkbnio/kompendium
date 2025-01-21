package io.bkbn.kompendium.core

import io.bkbn.kompendium.core.fixtures.TestHelpers
import io.bkbn.kompendium.core.util.nestedUnderRoot
import io.bkbn.kompendium.core.util.paramWrapper
import io.bkbn.kompendium.core.util.rootRoute
import io.bkbn.kompendium.core.util.simplePathParsing
import io.bkbn.kompendium.core.util.trailingSlash
import io.kotest.core.spec.style.DescribeSpec

class KompendiumRouteParsingTest : DescribeSpec({
  describe("Route Parsing") {
    it("Can parse a simple path and store it under the expected route") {
      TestHelpers.openApiTestAllSerializers("T0012__path_parser.json") { simplePathParsing() }
    }
    it("Can notarize the root route") {
      TestHelpers.openApiTestAllSerializers("T0013__root_route.json") { rootRoute() }
    }
    it("Can notarize a route under the root module without appending trailing slash") {
      TestHelpers.openApiTestAllSerializers("T0014__nested_under_root.json") { nestedUnderRoot() }
    }
    it("Can notarize a route with a trailing slash") {
      TestHelpers.openApiTestAllSerializers("T0015__trailing_slash.json") { trailingSlash() }
    }
    it("Can notarize a route with a parameter") {
      TestHelpers.openApiTestAllSerializers("T0068__param_wrapper.json") { paramWrapper() }
    }
  }
})
