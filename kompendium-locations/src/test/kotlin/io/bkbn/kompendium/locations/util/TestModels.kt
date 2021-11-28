package io.bkbn.kompendium.locations.util

import io.bkbn.kompendium.annotations.KompendiumParam
import io.bkbn.kompendium.annotations.ParamType
import io.ktor.locations.Location

@Location("/test/{name}")
data class SimpleLoc(@KompendiumParam(ParamType.PATH) val name: String) {
  @Location("/nesty")
  data class NestedLoc(@KompendiumParam(ParamType.QUERY) val isCool: Boolean, val parent: SimpleLoc)
}

data class SimpleResponse(val result: Boolean)
data class SimpleRequest(val input: String)
