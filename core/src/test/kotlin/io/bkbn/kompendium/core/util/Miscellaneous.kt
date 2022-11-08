package io.bkbn.kompendium.core.util

import io.bkbn.kompendium.core.fixtures.ColumnSchema
import io.bkbn.kompendium.core.fixtures.DateTimeString
import io.bkbn.kompendium.core.fixtures.ManyThings
import io.bkbn.kompendium.core.fixtures.Nested
import io.bkbn.kompendium.core.fixtures.NullableEnum
import io.bkbn.kompendium.core.fixtures.ProfileUpdateRequest
import io.bkbn.kompendium.core.fixtures.TestCreatedResponse
import io.bkbn.kompendium.core.fixtures.TestResponse
import io.bkbn.kompendium.core.fixtures.TestSimpleRequest
import io.bkbn.kompendium.core.metadata.GetInfo
import io.bkbn.kompendium.core.metadata.PutInfo
import io.bkbn.kompendium.core.plugin.NotarizedRoute
import io.bkbn.kompendium.core.util.TestModules.defaultParams
import io.bkbn.kompendium.core.util.TestModules.defaultPath
import io.bkbn.kompendium.core.util.TestModules.defaultPathDescription
import io.bkbn.kompendium.core.util.TestModules.defaultPathSummary
import io.bkbn.kompendium.core.util.TestModules.defaultRequestDescription
import io.bkbn.kompendium.core.util.TestModules.defaultResponseDescription
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.bkbn.kompendium.oas.payload.Parameter
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.install
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Routing
import io.ktor.server.routing.route

fun Routing.withOperationId() = basicGetGenerator<TestResponse>(operationId = "getThisDude")
fun Routing.nullableNestedObject() = basicGetGenerator<ProfileUpdateRequest>()
fun Routing.nullableEnumField() = basicGetGenerator<NullableEnum>()
fun Routing.nullableReference() = basicGetGenerator<ManyThings>()
fun Routing.dateTimeString() = basicGetGenerator<DateTimeString>()
fun Routing.headerParameter() = basicGetGenerator<TestResponse>(
  params = listOf(
    Parameter(
      name = "X-User-Email",
      `in` = Parameter.Location.header,
      schema = TypeDefinition.STRING,
      required = true
    )
  )
)

fun Routing.nestedTypeName() = basicGetGenerator<Nested.Response>()
fun Routing.topLevelNullable() = basicGetGenerator<TestResponse?>()
fun Routing.simpleRecursive() = basicGetGenerator<ColumnSchema>()
fun Routing.samePathDifferentMethodsAndAuth() {
  route(defaultPath) {
    install(NotarizedRoute()) {
      parameters = defaultParams
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
    authenticate("basic") {
      install(NotarizedRoute()) {
        put = PutInfo.builder {
          summary(defaultPathSummary)
          description(defaultPathDescription)
          request {
            description(defaultRequestDescription)
            requestType<TestSimpleRequest>()
          }
          response {
            description(defaultResponseDescription)
            responseCode(HttpStatusCode.Created)
            responseType<TestCreatedResponse>()
          }
        }
      }
    }
  }
}
