package io.bkbn.kompendium.core.routes

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import io.bkbn.kompendium.oas.OpenApiSpec
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route

/**
 * Provides an out-of-the-box route to return the generated [OpenApiSpec]
 * @param oas spec that is returned
 * @param om provider for Jackson
 */
// TODO Inline this into feature???
fun Routing.openApi(
  oas: OpenApiSpec,
  om: ObjectMapper = objectMapper
) {
  route("/openapi.json") {
    get {
      call.respondText { om.writeValueAsString(oas) }
    }
  }
}

private val objectMapper = ObjectMapper()
  .setSerializationInclusion(JsonInclude.Include.NON_NULL)
  .enable(SerializationFeature.INDENT_OUTPUT)
