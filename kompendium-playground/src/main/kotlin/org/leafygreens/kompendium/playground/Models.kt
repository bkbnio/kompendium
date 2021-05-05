package org.leafygreens.kompendium.playground

import org.leafygreens.kompendium.annotations.KompendiumField
import org.leafygreens.kompendium.annotations.PathParam
import org.leafygreens.kompendium.annotations.QueryParam

data class ExampleParams(
  @PathParam val id: Int,
  @QueryParam val name: String
)

data class JustQuery(
  @QueryParam val potato: Boolean,
  @QueryParam val tomato: String
)

data class ExampleNested(val nesty: String)

data class ExampleRequest(
  @KompendiumField(name = "field_name")
  val fieldName: ExampleNested,
  val b: Double,
  val aaa: List<Long>
)

data class ExampleResponse(val c: String)

data class ExceptionResponse(val message: String)

data class ExampleCreatedResponse(val id: Int, val c: String)
