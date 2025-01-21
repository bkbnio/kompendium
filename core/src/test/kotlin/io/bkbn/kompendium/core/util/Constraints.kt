package io.bkbn.kompendium.core.util

import io.bkbn.kompendium.core.fixtures.DoubleResponse
import io.bkbn.kompendium.core.fixtures.Page
import io.bkbn.kompendium.core.fixtures.TestCreatedResponse
import io.bkbn.kompendium.core.fixtures.TestNested
import io.bkbn.kompendium.core.metadata.GetInfo
import io.bkbn.kompendium.core.plugin.NotarizedRoute
import io.bkbn.kompendium.core.util.TestModules.defaultPath
import io.bkbn.kompendium.enrichment.CollectionEnrichment
import io.bkbn.kompendium.enrichment.NumberEnrichment
import io.bkbn.kompendium.enrichment.ObjectEnrichment
import io.bkbn.kompendium.enrichment.StringEnrichment
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.Route
import io.ktor.server.routing.route

fun Route.intConstraints() {
  route(defaultPath) {
    install(NotarizedRoute()) {
      get = GetInfo.builder {
        summary("Get an int")
        description("Get an int")
        response {
          responseCode(HttpStatusCode.OK)
          description("An int")
          responseType(
            enrichment = ObjectEnrichment("example") {
              TestCreatedResponse::id {
                NumberEnrichment("blah-blah-blah") {
                  minimum = 2
                  maximum = 100
                  multipleOf = 2
                }
              }
              responseCode(HttpStatusCode.OK)
            }
          )
        }
      }
    }
  }
}

fun Route.doubleConstraints() {
  route(defaultPath) {
    install(NotarizedRoute()) {
      get = GetInfo.builder {
        summary("Get a double")
        description("Get a double")
        response {
          responseCode(HttpStatusCode.OK)
          description("A double")
          responseType(
            enrichment = ObjectEnrichment("example") {
              DoubleResponse::payload {
                NumberEnrichment("blah-blah-blah") {
                  minimum = 2.0
                  maximum = 100.0
                  multipleOf = 2.0
                }
              }
            }
          )
          responseCode(HttpStatusCode.OK)
        }
      }
    }
  }
}

fun Route.stringConstraints() {
  route(defaultPath) {
    install(NotarizedRoute()) {
      get = GetInfo.builder {
        summary("Get a string")
        description("Get a string with constraints")
        response {
          responseCode(HttpStatusCode.OK)
          description("A string")
          responseType(
            enrichment = ObjectEnrichment("example") {
              TestNested::nesty {
                StringEnrichment("blah") {
                  maxLength = 10
                  minLength = 2
                }
              }
            }
          )
          responseCode(HttpStatusCode.OK)
        }
      }
    }
  }
}

fun Route.stringPatternConstraints() {
  route(defaultPath) {
    install(NotarizedRoute()) {
      get = GetInfo.builder {
        summary("Get a string")
        description("This is a description")
        response {
          responseCode(HttpStatusCode.OK)
          description("A string")
          responseType(
            enrichment = ObjectEnrichment("example") {
              TestNested::nesty {
                StringEnrichment("blah") {
                  pattern = "[a-z]+"
                }
              }
            }
          )
          responseCode(HttpStatusCode.OK)
        }
      }
    }
  }
}

fun Route.stringContentEncodingConstraints() {
  route(defaultPath) {
    install(NotarizedRoute()) {
      get = GetInfo.builder {
        summary("Get a string")
        description("This is a description")
        response {
          responseCode(HttpStatusCode.OK)
          description("A string")
          responseType(
            enrichment = ObjectEnrichment("example") {
              TestNested::nesty {
                StringEnrichment("blah") {
                  contentEncoding = "base64"
                  contentMediaType = "image/png"
                }
              }
            }
          )
          responseCode(HttpStatusCode.OK)
        }
      }
    }
  }
}

fun Route.arrayConstraints() {
  route(defaultPath) {
    install(NotarizedRoute()) {
      get = GetInfo.builder {
        summary("Get an array")
        description("Get an array of strings")
        response {
          responseCode(HttpStatusCode.OK)
          description("An array")
          responseType(
            enrichment = ObjectEnrichment("example") {
              Page<String>::content {
                CollectionEnrichment<String>("blah") {
                  minItems = 2
                  maxItems = 10
                  uniqueItems = true
                }
              }
            }
          )
          responseCode(HttpStatusCode.OK)
        }
      }
    }
  }
}
