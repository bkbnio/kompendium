package org.leafygreens.kompendium.auth.util

import org.leafygreens.kompendium.annotations.KompendiumField
import org.leafygreens.kompendium.annotations.PathParam
import org.leafygreens.kompendium.annotations.QueryParam

data class TestParams(
  @PathParam val a: String,
  @QueryParam val aa: Int
)

data class TestRequest(
  @KompendiumField(name = "field_name")
  val b: Double,
  val aaa: List<Long>
)

data class TestResponse(val c: String)

