package io.bkbn.kompendium.core.util

import io.bkbn.kompendium.core.metadata.GetInfo
import io.bkbn.kompendium.core.plugin.NotarizedRoute
import io.bkbn.kompendium.core.util.TestModules.defaultPathDescription
import io.bkbn.kompendium.core.util.TestModules.defaultPathSummary
import io.bkbn.kompendium.core.util.TestModules.defaultResponseDescription
import io.bkbn.kompendium.core.util.TestModules.rootPath
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.bkbn.kompendium.oas.payload.Parameter
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.Route
import io.ktor.server.routing.Routing
import io.ktor.server.routing.route

object TestModules {

  const val defaultPath = "/test/{a}"
  const val rootPath = "/"
  const val defaultResponseDescription = "A Successful Endeavor"
  const val defaultRequestDescription = "You gotta send it"
  const val defaultPathSummary = "Great Summary!"
  const val defaultPathDescription = "testing more"

  val defaultParams = listOf(
    Parameter(
      name = "a",
      `in` = Parameter.Location.path,
      schema = TypeDefinition.STRING,
    ),
    Parameter(
      name = "aa",
      `in` = Parameter.Location.query,
      schema = TypeDefinition.INT
    )
  )
}

internal inline fun <reified T> Routing.basicGetGenerator(
  params: List<Parameter> = emptyList(),
  operationId: String? = null
) {
  route(rootPath) {
    basicGetGenerator<T>(params, operationId)
  }
}

internal inline fun <reified T> Route.basicGetGenerator(
  params: List<Parameter> = emptyList(),
  operationId: String? = null
) {
  install(NotarizedRoute()) {
    get = GetInfo.builder {
      summary(defaultPathSummary)
      description(defaultPathDescription)
      operationId?.let { operationId(it) }
      parameters = params
      response {
        description(defaultResponseDescription)
        responseCode(HttpStatusCode.OK)
        responseType<T>()
      }
    }
  }
}
