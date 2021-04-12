package org.leafygreens.kompendium

import org.leafygreens.kompendium.models.OpenApiSpec
import org.leafygreens.kompendium.models.OpenApiSpecInfo

class Kompendium {
  val spec = OpenApiSpec(
    info = OpenApiSpecInfo(),
    servers = mutableListOf(),
    paths = mutableMapOf()
  )
}
