package io.bkbn.kompendium.core.plugin

import io.bkbn.kompendium.core.metadata.GetInfo
import io.bkbn.kompendium.core.attribute.KompendiumAttributes
import io.bkbn.kompendium.oas.path.Path
import io.bkbn.kompendium.oas.path.PathOperation
import io.bkbn.kompendium.oas.payload.Parameter
import io.ktor.server.application.createRouteScopedPlugin

object NotarizedRoute {

  class Config {
    // TODO Would be preferable to introspect from installation path
    lateinit var path: String
    var tags: Set<String> = emptySet()
    var parameters: List<Parameter> = emptyList()
    var get: GetInfo? = null
    // todo get, post, put, etc.
  }

  operator fun invoke() = createRouteScopedPlugin(
    name = "NotarizedRoute",
    createConfiguration = ::Config
  ) {
    val spec = application.attributes[KompendiumAttributes.openApiSpec]

    require(spec.paths[pluginConfig.path] == null) {
      """
        The specified path ${pluginConfig.path} has already been documented!
        Please make sure that all notarized paths are unique
      """.trimIndent()
    }

    val path = Path()
    path.parameters = pluginConfig.parameters

    pluginConfig.get?.let { get ->
      path.get = PathOperation(
        tags = pluginConfig.tags.plus(get.tags),
        summary = get.summary,
        description = get.description,
        externalDocs = get.externalDocumentation,
        operationId = get.operationId,
        deprecated = get.deprecated,
        parameters = get.parameters
      )
    }

    // todo handle get, post, put, etc.

    spec.paths[pluginConfig.path] = path
  }

}
