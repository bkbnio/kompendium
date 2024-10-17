package io.bkbn.kompendium.core.util

import io.bkbn.kompendium.core.fixtures.DefaultField
import io.bkbn.kompendium.core.fixtures.NullableField
import io.bkbn.kompendium.core.fixtures.TestResponse
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.bkbn.kompendium.oas.payload.Parameter
import io.ktor.server.routing.Route

fun Route.requiredParams() = basicGetGenerator<TestResponse>(
  params = listOf(
    Parameter(
      name = "id",
      `in` = Parameter.Location.path,
      schema = TypeDefinition.STRING
    )
  )
)

fun Route.nonRequiredParam() = basicGetGenerator<TestResponse>(
  params = listOf(
    Parameter(
      name = "id",
      `in` = Parameter.Location.query,
      schema = TypeDefinition.STRING,
      required = false
    )
  )
)

fun Route.defaultField() = basicGetGenerator<DefaultField>()
fun Route.nullableField() = basicGetGenerator<NullableField>()
