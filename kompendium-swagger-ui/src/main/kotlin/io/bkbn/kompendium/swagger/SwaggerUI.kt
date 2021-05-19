package io.bkbn.kompendium.swagger

import io.ktor.application.call
import io.ktor.response.respondRedirect
import io.ktor.routing.Routing
import io.ktor.routing.get

fun Routing.swaggerUI(openApiJsonUrl: String = "/openapi.json") {
  get("/swagger-ui") {
    call.respondRedirect("/webjars/swagger-ui/index.html?url=$openApiJsonUrl", true)
  }
}
