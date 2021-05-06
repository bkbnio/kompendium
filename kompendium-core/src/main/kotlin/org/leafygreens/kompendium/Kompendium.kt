package org.leafygreens.kompendium

import org.leafygreens.kompendium.models.meta.ErrorMap
import org.leafygreens.kompendium.models.meta.SchemaMap
import org.leafygreens.kompendium.models.oas.OpenApiSpec
import org.leafygreens.kompendium.models.oas.OpenApiSpecInfo
import org.leafygreens.kompendium.path.CorePathCalculator
import org.leafygreens.kompendium.path.PathCalculator

/**
 * Maintains all state for the Kompendium library
 */
object Kompendium {

  var errorMap: ErrorMap = emptyMap()
  var cache: SchemaMap = emptyMap()

  var openApiSpec = OpenApiSpec(
    info = OpenApiSpecInfo(),
    servers = mutableListOf(),
    paths = mutableMapOf()
  )

  var pathCalculator: PathCalculator = CorePathCalculator()

  fun resetSchema() {
    openApiSpec = OpenApiSpec(
      info = OpenApiSpecInfo(),
      servers = mutableListOf(),
      paths = mutableMapOf()
    )
    cache = emptyMap()
  }
}
