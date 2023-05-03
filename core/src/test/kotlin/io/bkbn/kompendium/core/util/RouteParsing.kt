package io.bkbn.kompendium.core.util

import io.bkbn.kompendium.core.fixtures.TestResponse
import io.bkbn.kompendium.core.metadata.GetInfo
import io.bkbn.kompendium.core.plugin.NotarizedRoute
import io.bkbn.kompendium.core.util.TestModules.defaultParams
import io.bkbn.kompendium.core.util.TestModules.defaultPathDescription
import io.bkbn.kompendium.core.util.TestModules.defaultPathSummary
import io.bkbn.kompendium.core.util.TestModules.defaultResponseDescription
import io.bkbn.kompendium.core.util.TestModules.rootPath
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.bkbn.kompendium.oas.payload.Parameter
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.install
import io.ktor.server.routing.Routing
import io.ktor.server.routing.route
import io.ktor.server.routing.param

fun Routing.simplePathParsing() {
  route("/this") {
    route("/is") {
      route("/a") {
        route("/complex") {
          route("path") {
            route("with/an/{id}") {
              install(NotarizedRoute()) {
                get = GetInfo.builder {
                  parameters = listOf(
                    Parameter(
                      name = "id",
                      `in` = Parameter.Location.path,
                      schema = TypeDefinition.STRING
                    )
                  )
                  summary(defaultPathSummary)
                  description(defaultPathDescription)
                  response {
                    description(defaultResponseDescription)
                    responseCode(HttpStatusCode.OK)
                    responseType<TestResponse>()
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

fun Routing.rootRoute() {
  route(rootPath) {
    install(NotarizedRoute()) {
      parameters = listOf(defaultParams.last())
      get = GetInfo.builder {
        summary(defaultPathSummary)
        description(defaultPathDescription)
        response {
          description(defaultResponseDescription)
          responseCode(HttpStatusCode.OK)
          responseType<TestResponse>()
        }
      }
    }
  }
}

fun Routing.nestedUnderRoot() {
  route("/") {
    route("/testerino") {
      install(NotarizedRoute()) {
        get = GetInfo.builder {
          summary(defaultPathSummary)
          description(defaultPathDescription)
          response {
            description(defaultResponseDescription)
            responseCode(HttpStatusCode.OK)
            responseType<TestResponse>()
          }
        }
      }
    }
  }
}

fun Routing.trailingSlash() {
  route("/test") {
    route("/") {
      install(NotarizedRoute()) {
        get = GetInfo.builder {
          summary(defaultPathSummary)
          description(defaultPathDescription)
          response {
            description(defaultResponseDescription)
            responseCode(HttpStatusCode.OK)
            responseType<TestResponse>()
          }
        }
      }
    }
  }
}

fun Routing.parameter() {
  route("/test") {
    param("test") {
      install(NotarizedRoute()) {
        parameters = listOf(
          Parameter(
            name = "test",
            `in` = Parameter.Location.query,
            schema = TypeDefinition.STRING
          )
        )
        get = GetInfo.builder {
          summary(defaultPathSummary)
          description(defaultPathDescription)
          response {
            description(defaultResponseDescription)
            responseCode(HttpStatusCode.OK)
            responseType<TestResponse>()
          }
        }
      }
    }
  }
}
