package io.bkbn.kompendium.core.util

import io.bkbn.kompendium.core.fixtures.DoubleResponse
import io.bkbn.kompendium.core.fixtures.Page
import io.bkbn.kompendium.core.fixtures.TestCreatedResponse
import io.bkbn.kompendium.core.fixtures.TestNested
import io.bkbn.kompendium.core.metadata.GetInfo
import io.bkbn.kompendium.core.plugin.NotarizedRoute
import io.bkbn.kompendium.core.util.TestModules.defaultPath
import io.bkbn.kompendium.enrichment.TypeEnrichment
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.install
import io.ktor.server.routing.Routing
import io.ktor.server.routing.route

fun Routing.intConstraints() {
  route(defaultPath) {
    install(NotarizedRoute()) {
      get = GetInfo.builder {
        summary("Get an int")
        description("Get an int")
        response {
          responseCode(HttpStatusCode.OK)
          description("An int")
          responseType(
            enrichment = TypeEnrichment("example") {
              TestCreatedResponse::id {
                minimum = 2
                maximum = 100
                multipleOf = 2
              }
            }
          )
          responseCode(HttpStatusCode.OK)
        }
      }
    }
  }
}

fun Routing.doubleConstraints() {
  route(defaultPath) {
    install(NotarizedRoute()) {
      get = GetInfo.builder {
        summary("Get a double")
        description("Get a double")
        response {
          responseCode(HttpStatusCode.OK)
          description("A double")
          responseType(
            enrichment = TypeEnrichment("example") {
              DoubleResponse::payload {
                minimum = 2.0
                maximum = 100.0
                multipleOf = 2.0
              }
            }
          )
          responseCode(HttpStatusCode.OK)
        }
      }
    }
  }
}

fun Routing.stringConstraints() {
  route(defaultPath) {
    install(NotarizedRoute()) {
      get = GetInfo.builder {
        summary("Get a string")
        description("Get a string with constraints")
        response {
          responseCode(HttpStatusCode.OK)
          description("A string")
          responseType(
            enrichment = TypeEnrichment("example") {
              TestNested::nesty {
                maxLength = 10
                minLength = 2
              }
            }
          )
          responseCode(HttpStatusCode.OK)
        }
      }
    }
  }
}

fun Routing.stringPatternConstraints() {
  route(defaultPath) {
    install(NotarizedRoute()) {
      get = GetInfo.builder {
        summary("Get a string")
        description("This is a description")
        response {
          responseCode(HttpStatusCode.OK)
          description("A string")
          responseType(
            enrichment = TypeEnrichment("example") {
              TestNested::nesty {
                pattern = "[a-z]+"
              }
            }
          )
          responseCode(HttpStatusCode.OK)
        }
      }
    }
  }
}

fun Routing.stringContentEncodingConstraints() {
  route(defaultPath) {
    install(NotarizedRoute()) {
      get = GetInfo.builder {
        summary("Get a string")
        description("This is a description")
        response {
          responseCode(HttpStatusCode.OK)
          description("A string")
          responseType(
            enrichment = TypeEnrichment("example") {
              TestNested::nesty {
                contentEncoding = "base64"
                contentMediaType = "image/png"
              }
            }
          )
          responseCode(HttpStatusCode.OK)
        }
      }
    }
  }
}

fun Routing.arrayConstraints() {
  route(defaultPath) {
    install(NotarizedRoute()) {
      get = GetInfo.builder {
        summary("Get an array")
        description("Get an array of strings")
        response {
          responseCode(HttpStatusCode.OK)
          description("An array")
          responseType(
            enrichment = TypeEnrichment("example") {
              Page<String>::content {
                minItems = 2
                maxItems = 10
                uniqueItems = true
              }
            }
          )
          responseCode(HttpStatusCode.OK)
        }
      }
    }
  }
}
