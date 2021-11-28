package io.bkbn.kompendium.core

import io.bkbn.kompendium.core.metadata.ErrorMap
import io.bkbn.kompendium.core.metadata.SchemaMap
import io.bkbn.kompendium.oas.OpenApiSpec
import io.bkbn.kompendium.oas.info.Info
import io.bkbn.kompendium.oas.schema.TypedSchema
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
    info = Info(),
    servers = mutableListOf(),
    paths = mutableMapOf()
  )

  fun resetSchema() {
    openApiSpec = OpenApiSpec(
      info = Info(),
      servers = mutableListOf(),
      paths = mutableMapOf()
    )
    cache = emptyMap()
  }

  fun addCustomTypeSchema(clazz: KClass<*>, schema: TypedSchema) {
    cache = cache.plus(clazz.simpleName!! to schema)
  }
}
