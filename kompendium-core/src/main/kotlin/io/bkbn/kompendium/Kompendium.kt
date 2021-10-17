package io.bkbn.kompendium

import io.bkbn.kompendium.models.meta.ErrorMap
import io.bkbn.kompendium.models.meta.SchemaMap
import io.bkbn.kompendium.models.oas.OpenApiSpec
import io.bkbn.kompendium.models.oas.OpenApiSpecInfo
import io.bkbn.kompendium.models.oas.TypedSchema
import io.bkbn.kompendium.path.IPathCalculator
import io.bkbn.kompendium.path.PathCalculator
import io.ktor.routing.Route
import io.ktor.routing.RouteSelector
import kotlin.reflect.KClass

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
