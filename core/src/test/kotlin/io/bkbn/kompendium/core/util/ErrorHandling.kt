package io.bkbn.kompendium.core.util

import io.bkbn.kompendium.core.fixtures.TestResponse
import io.bkbn.kompendium.core.util.TestModules.defaultPath
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.route

fun Route.samePathSameMethod() {
  route(defaultPath) {
    basicGetGenerator<TestResponse>()
    authenticate("basic") {
      basicGetGenerator<TestResponse>()
    }
  }
}
