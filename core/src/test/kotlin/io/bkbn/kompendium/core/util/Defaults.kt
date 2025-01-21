package io.bkbn.kompendium.core.util

import io.bkbn.kompendium.core.fixtures.TestResponse
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.bkbn.kompendium.oas.payload.Parameter
import io.ktor.server.routing.Route

fun Route.defaultParameter() = basicGetGenerator<TestResponse>(
  params = listOf(
    Parameter(
      name = "id",
      `in` = Parameter.Location.path,
      schema = TypeDefinition.STRING.withDefault("IDK")
    )
  )
)
