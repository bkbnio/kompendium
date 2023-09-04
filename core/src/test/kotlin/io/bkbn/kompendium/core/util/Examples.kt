package io.bkbn.kompendium.core.util

import io.bkbn.kompendium.core.fixtures.TestNested
import io.bkbn.kompendium.core.fixtures.TestRequest
import io.bkbn.kompendium.core.fixtures.TestResponse
import io.bkbn.kompendium.core.metadata.PostInfo
import io.bkbn.kompendium.core.plugin.NotarizedRoute
import io.bkbn.kompendium.core.util.TestModules.defaultPathDescription
import io.bkbn.kompendium.core.util.TestModules.defaultPathSummary
import io.bkbn.kompendium.core.util.TestModules.defaultRequestDescription
import io.bkbn.kompendium.core.util.TestModules.defaultResponseDescription
import io.bkbn.kompendium.core.util.TestModules.rootPath
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.bkbn.kompendium.oas.payload.MediaType
import io.bkbn.kompendium.oas.payload.Parameter
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.install
import io.ktor.server.routing.Routing
import io.ktor.server.routing.route

fun Routing.reqRespExamples() {
  route(rootPath) {
    install(NotarizedRoute()) {
      post = PostInfo.builder {
        summary(defaultPathSummary)
        description(defaultPathDescription)
        request {
          description(defaultRequestDescription)
          requestType<TestRequest>()
          examples(
            "Testerina" to MediaType.Example(TestRequest(TestNested("asdf"), 1.5, emptyList()))
          )
        }
        response {
          description(defaultResponseDescription)
          responseCode(HttpStatusCode.OK)
          responseType<TestResponse>()
          examples(
            "Testerino" to MediaType.Example(TestResponse("Heya"))
          )
        }
      }
    }
  }
}

fun Routing.exampleParams() = basicGetGenerator<TestResponse>(
  params = listOf(
    Parameter(
      name = "id",
      `in` = Parameter.Location.path,
      schema = TypeDefinition.STRING,
      examples = mapOf(
        "foo" to MediaType.Example("testing")
      )
    )
  )
)

fun Routing.optionalReqExample() {
  route(rootPath) {
    install(NotarizedRoute()) {
      post = PostInfo.builder {
        summary(defaultPathSummary)
        description(defaultPathDescription)
        request {
          description(defaultRequestDescription)
          requestType<TestRequest>()
          examples(
            "Testerina" to MediaType.Example(TestRequest(TestNested("asdf"), 1.5, emptyList()))
          )
          required(false)
        }
        response {
          description(defaultResponseDescription)
          responseCode(HttpStatusCode.OK)
          responseType<TestResponse>()
          examples(
            "Testerino" to MediaType.Example(TestResponse("Heya"))
          )
        }
      }
    }
  }
}

fun Routing.exampleSummaryAndDescription() {
  route(rootPath) {
    install(NotarizedRoute()) {
      post = PostInfo.builder {
        summary("This is a summary")
        description("This is a description")
        request {
          description("This is a request description")
          requestType<TestRequest>()
          examples(
            "Testerina" to MediaType.Example(
              TestRequest(TestNested("asdf"), 1.5, emptyList()),
              "summary",
              "description"
            )
          )
        }
        response {
          description("This is a response description")
          responseCode(HttpStatusCode.OK)
          responseType<TestResponse>()
          examples(
            "Testerino" to MediaType.Example(TestResponse("Heya"), "summary", "description")
          )
        }
      }
    }
  }
}
