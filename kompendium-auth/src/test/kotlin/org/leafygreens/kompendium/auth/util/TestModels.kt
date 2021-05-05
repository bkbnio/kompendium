package org.leafygreens.kompendium.auth.util

import org.leafygreens.kompendium.annotations.KompendiumField
import org.leafygreens.kompendium.annotations.KompendiumParam
import org.leafygreens.kompendium.annotations.ParamType

data class TestParams(
  @KompendiumParam(ParamType.PATH) val a: String,
  @KompendiumParam(ParamType.QUERY) val aa: Int
)

data class TestRequest(
  @KompendiumField(name = "field_name")
  val b: Double,
  val aaa: List<Long>
)

data class TestResponse(val c: String)

