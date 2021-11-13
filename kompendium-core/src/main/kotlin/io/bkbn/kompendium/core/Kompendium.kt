package io.bkbn.kompendium.core

import io.bkbn.kompendium.core.metadata.ErrorMap
import io.bkbn.kompendium.core.metadata.SchemaMap
import io.bkbn.kompendium.core.path.IPathCalculator
import io.bkbn.kompendium.core.path.PathCalculator
import io.ktor.routing.Route
import io.ktor.routing.RouteSelector
import kotlin.reflect.KClass
import io.bkbn.kompendium.oas.old.OpenApiSpec
import io.bkbn.kompendium.oas.old.OpenApiSpecInfo
import io.bkbn.kompendium.oas.old.TypedSchema

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

  fun calculatePath(route: Route) = PathCalculator.calculate(route)

  fun resetSchema() {
    openApiSpec = OpenApiSpec(
      info = OpenApiSpecInfo(),
      servers = mutableListOf(),
      paths = mutableMapOf()
    )
    cache = emptyMap()
  }

  fun addCustomTypeSchema(clazz: KClass<*>, schema: TypedSchema) {
    cache = cache.plus(clazz.simpleName!! to schema)
  }

  fun <T : RouteSelector> addCustomRouteHandler(
    selector: KClass<T>,
    handler: IPathCalculator.(Route, String) -> String
  ) {
    PathCalculator.addCustomRouteHandler(selector, handler)
  }
}
