package io.bkbn.kompendium.auth.util

import io.bkbn.kompendium.annotations.KompendiumField
import io.bkbn.kompendium.annotations.KompendiumParam
import io.bkbn.kompendium.annotations.ParamType

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

