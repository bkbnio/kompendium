package org.leafygreens.kompendium.routes

import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import org.leafygreens.kompendium.models.oas.OpenApiSpec

fun Routing.openApi(oas: OpenApiSpec) {
  route("/openapi.json") {
    get {
      call.respond(oas)
    }
  }
}
