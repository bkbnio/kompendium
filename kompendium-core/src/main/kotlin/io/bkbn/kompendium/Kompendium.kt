package io.bkbn.kompendium

import io.bkbn.kompendium.models.meta.ErrorMap
import io.bkbn.kompendium.models.meta.SchemaMap
import io.bkbn.kompendium.models.oas.OpenApiSpec
import io.bkbn.kompendium.models.oas.OpenApiSpecInfo
import io.bkbn.kompendium.path.CorePathCalculator
import io.bkbn.kompendium.path.PathCalculator

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
